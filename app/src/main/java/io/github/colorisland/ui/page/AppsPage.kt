package io.github.colorisland.ui.page

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import io.github.colorisland.R
import io.github.colorisland.data.AppEntry
import io.github.colorisland.data.AppsRepository
import io.github.colorisland.data.ChannelItem
import io.github.colorisland.util.ChannelCatalog
import io.github.colorisland.data.PrefsRepository
import io.github.colorisland.ui.component.CollapsingPage
import io.github.colorisland.ui.component.SectionTitle
import io.github.colorisland.ui.component.SettingsCard
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 应用管理页：按应用开关灵动岛，可展开细配通知渠道。
 *
 * 数据流：
 * - 应用列表：AppsRepository.loadLaunchableApps()（PackageManager 查询）；
 * - 渠道目录：从 SharedPreferences 的 channel_catalog JSON 解析
 *   （该数据由 SystemUI 进程的 ChannelRegistry 持久化，结构为
 *   {pkg: [{id, name, count, lastSeen}, ...]}）；
 * - 禁用状态：repo.disabledApps / repo.disabledChannels，
 *   渠道禁用键格式为 "pkg:channelId"（与 PrefsRepository.setChannelEnabled 一致）。
 *
 * @param repo         配置仓库
 * @param prefsVersion 配置版本号，变化时刷新禁用状态
 */
@Composable
fun AppsPage(
    repo: PrefsRepository,
    prefsVersion: Int,
) {
    val context = LocalContext.current
    var apps by remember { mutableStateOf<List<AppEntry>>(emptyList()) }
    var query by remember { mutableStateOf("") }
    // 渠道目录：pkg -> 渠道列表（从 prefs 的 channel_catalog 解析）
    var catalog by remember { mutableStateOf<Map<String, List<ChannelItem>>>(emptyMap()) }
    // 当前展开的应用（点击行展开/收起渠道列表）
    var expanded by remember { mutableStateOf<String?>(null) }

    // 加载应用列表与渠道目录（进入页面时执行一次）
    LaunchedEffect(Unit) {
        apps = AppsRepository.INSTANCE.loadLaunchableApps(context)
        catalog = ChannelCatalog.parse(repo.prefs.getString("channel_catalog", null))
    }

    // 搜索过滤：匹配应用名或包名
    val filtered = remember(apps, query) {
        if (query.isBlank()) {
            apps
        } else {
            apps.filter {
                it.label.contains(query, ignoreCase = true) ||
                    it.pkg.contains(query, ignoreCase = true)
            }
        }
    }

    CollapsingPage(
        title = stringResource(R.string.apps_title),
        subtitle = "",
    ) {
        // ---- 搜索框 ----
        item {
            TextField(
                value = query,
                onValueChange = { query = it },
                label = stringResource(R.string.apps_search_hint),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )
        }

        // ---- 批量操作 ----
        item {
            SectionTitle(stringResource(R.string.apps_enable_all))
            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TextButton(
                        text = stringResource(R.string.apps_enable_all),
                        modifier = Modifier.weight(1f),
                        // 仓库方法无参数：清空禁用列表即全部启用
                        onClick = { repo.setAllAppsEnabled() },
                    )
                    TextButton(
                        text = stringResource(R.string.apps_disable_all),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            // 仓库无"全部禁用"方法，遍历当前列表逐个禁用
                            apps.forEach { entry ->
                                repo.setAppEnabled(entry.pkg, false)
                            }
                        },
                    )
                }
            }
        }

        // ---- 应用列表 ----
        if (filtered.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.apps_empty),
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceSecondary,
                    modifier = Modifier.padding(20.dp),
                )
            }
        } else {
            items(filtered, key = { it.pkg }) { entry ->
                AppRow(
                    entry = entry,
                    // 不在禁用列表即启用
                    enabled = entry.pkg !in repo.disabledApps,
                    channels = catalog[entry.pkg].orEmpty(),
                    disabledChannels = repo.disabledChannels,
                    expanded = expanded == entry.pkg,
                    onToggle = { repo.setAppEnabled(entry.pkg, it) },
                    onExpandToggle = {
                        expanded = if (expanded == entry.pkg) null else entry.pkg
                    },
                    onChannelToggle = { channelId, enabled ->
                        repo.setChannelEnabled(entry.pkg, channelId, enabled)
                    },
                )
            }
        }
    }
}

/**
 * 单个应用行：图标 + 名称/包名 + 启用开关；点击展开渠道细配。
 */
@Composable
private fun AppRow(
    entry: AppEntry,
    enabled: Boolean,
    channels: List<ChannelItem>,
    disabledChannels: Set<String>,
    expanded: Boolean,
    onToggle: (Boolean) -> Unit,
    onExpandToggle: () -> Unit,
    onChannelToggle: (channelId: String, enabled: Boolean) -> Unit,
) {
    SettingsCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 应用主行
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandToggle() }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // 应用图标（Drawable -> ImageBitmap）
                val icon: ImageBitmap? = remember(entry.pkg) { entry.icon.toImageBitmap() }
                if (icon != null) {
                    Image(
                        bitmap = icon,
                        contentDescription = entry.label,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp)),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.label,
                        style = MiuixTheme.textStyles.body1,
                    )
                    Text(
                        text = entry.pkg,
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceSecondary,
                    )
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = onToggle,
                )
            }

            // 展开的渠道列表
            if (expanded && channels.isNotEmpty()) {
                Text(
                    text = stringResource(
                        R.string.apps_channels_expand,
                        channels.size,
                    ),
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onSurfaceSecondary,
                    modifier = Modifier.padding(start = 68.dp, top = 4.dp, bottom = 4.dp),
                )
                channels.forEach { channel ->
                    // 禁用键格式与 PrefsRepository.setChannelEnabled 保持一致
                    val channelKey = entry.pkg + ":" + channel.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 68.dp, top = 6.dp, end = 16.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = channel.name.ifBlank { channel.id },
                            style = MiuixTheme.textStyles.body2,
                            modifier = Modifier.weight(1f),
                        )
                        Switch(
                            checked = channelKey !in disabledChannels,
                            onCheckedChange = { onChannelToggle(channel.id, it) },
                        )
                    }
                }
            } else if (expanded) {
                Text(
                    text = stringResource(R.string.apps_channels_collapse),
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onSurfaceSecondary,
                    modifier = Modifier.padding(start = 68.dp, top = 4.dp, bottom = 8.dp),
                )
            }
        }
    }
}

/** Drawable 安全转换为 ImageBitmap（失败返回 null，不阻断列表渲染） */
private fun Drawable?.toImageBitmap(): ImageBitmap? =
    try {
        this?.toBitmap(128, 128)?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
