package io.github.colorisland.ui.page

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import io.github.colorisland.ConfigSyncApp
import io.github.colorisland.R
import io.github.colorisland.data.PrefsRepository
import io.github.colorisland.ui.component.CollapsingPage
import io.github.colorisland.ui.component.PreferenceAction
import io.github.colorisland.ui.component.PreferenceSwitch
import io.github.colorisland.ui.component.SectionTitle
import io.github.colorisland.ui.component.SettingsCard
import io.github.colorisland.util.NotificationHelper
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Info
import top.yukonga.miuix.kmp.icon.extended.Play
import top.yukonga.miuix.kmp.icon.extended.Refresh
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 首页：模块状态 + 总开关 + 系统状态 + 工具。
 *
 * 与 v1.0 首页的对应关系：
 * - 模块状态：改用 ConfigSyncApp.xposedServiceBound（Xposed 服务已连接
 *   即表示模块已在 SystemUI 进程生效），比 v1.0 的推断方式更可靠；
 * - 测试通知：保留 NotificationHelper.sendTest，并补齐 Android 13+
 *   的 POST_NOTIFICATIONS 运行时权限申请（v1.0 缺少权限检查，属已修复缺陷）；
 * - 重启 SystemUI：保留 restartSystemUI（需要 Root）。
 *
 * @param repo         配置仓库（读写 SharedPreferences）
 * @param prefsVersion 配置版本号，变化时触发整页刷新（由 ColorIslandApp 维护）
 */
@Composable
fun HomePage(
    repo: PrefsRepository,
    prefsVersion: Int,
) {
    val context = LocalContext.current
    val notificationHelper = remember { NotificationHelper.INSTANCE }
    // 操作结果提示（如"已发送测试通知"）
    var message by remember { mutableStateOf<String?>(null) }

    // 回调中使用的字符串：在组合作用域提取，避免 LocalContext 直取资源
    // （lint: LocalContextGetResourceValueCall —— 配置变更时 LocalContext 资源可能过期）
    val testSentText = stringResource(R.string.test_sent)
    val testIslandDescText = stringResource(R.string.test_island_desc)
    val restartRequestedText = stringResource(R.string.restart_systemui) + " 已请求"

    // Android 13+ 通知运行时权限申请器
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            notificationHelper.sendTest(context)
            message = testSentText
        } else {
            message = testIslandDescText
        }
    }

    CollapsingPage(
        title = stringResource(R.string.app_name),
        subtitle = stringResource(R.string.home_subtitle),
    ) {
        // ---- 模块状态 ----
        item {
            SectionTitle(stringResource(R.string.module_description))
            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.module_description),
                        style = MiuixTheme.textStyles.body1,
                    )
                    // Xposed 服务已连接 => 模块已在 SystemUI 进程生效
                    val app = context.applicationContext as? ConfigSyncApp
                    val active = app?.xposedServiceBound == true
                    Text(
                        text = if (active) "已生效" else "未生效",
                        style = MiuixTheme.textStyles.body1,
                        color = if (active) {
                            MiuixTheme.colorScheme.primary
                        } else {
                            MiuixTheme.colorScheme.error
                        },
                    )
                }
            }
        }

        // ---- 总开关 ----
        item {
            SectionTitle(stringResource(R.string.master_switch))
            SettingsCard {
                PreferenceSwitch(
                    title = stringResource(R.string.master_switch),
                    summary = stringResource(R.string.master_switch_desc),
                    icon = MiuixIcons.Settings,
                    checked = repo.masterEnabled,
                    onCheckedChange = { repo.updateMasterEnabled(it) },
                )
            }
        }

        // ---- 系统状态（充电/热点/手电筒/录屏）----
        item {
            SectionTitle(stringResource(R.string.sys_status_section_title))
            SettingsCard {
                PreferenceSwitch(
                    title = stringResource(R.string.sys_status_enabled),
                    summary = stringResource(R.string.sys_status_enabled_desc),
                    icon = MiuixIcons.Info,
                    checked = repo.systemStatusEnabled,
                    onCheckedChange = { repo.updateSystemStatusEnabled(it) },
                )
                PreferenceSwitch(
                    title = stringResource(R.string.sys_status_persistent),
                    summary = stringResource(R.string.sys_status_persistent_desc),
                    icon = MiuixIcons.Play,
                    checked = repo.systemStatusPersistent,
                    enabled = repo.systemStatusEnabled,
                    onCheckedChange = { repo.updateSystemStatusPersistent(it) },
                )
                // 四个系统状态类型开关（键名与 IslandConfig 约定一致）
                val statusItems = listOf(
                    Triple(R.string.sys_status_charging, R.string.sys_status_charging_desc, islandConfigKeys[0]),
                    Triple(R.string.sys_status_hotspot, R.string.sys_status_hotspot_desc, islandConfigKeys[1]),
                    Triple(R.string.sys_status_flashlight, R.string.sys_status_flashlight_desc, islandConfigKeys[2]),
                    Triple(R.string.sys_status_screen_record, R.string.sys_status_screen_record_desc, islandConfigKeys[3]),
                )
                statusItems.forEach { (titleRes, descRes, key) ->
                    PreferenceSwitch(
                        title = stringResource(titleRes),
                        summary = stringResource(descRes),
                        icon = null,
                        checked = repo.statusFlags[key] ?: true,
                        enabled = repo.systemStatusEnabled,
                        onCheckedChange = { repo.updateStatusFlag(key, it) },
                    )
                }
            }
        }

        // ---- 工具 ----
        item {
            SectionTitle(stringResource(R.string.tools_section_title))
            SettingsCard {
                PreferenceAction(
                    title = stringResource(R.string.test_island),
                    summary = stringResource(R.string.test_island_desc),
                    icon = MiuixIcons.Play,
                ) {
                    // minSdk=33：需动态申请 POST_NOTIFICATIONS，否则 sendTest 静默失败
                    if (
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS,
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        notificationHelper.sendTest(context)
                        message = testSentText
                    }
                }
                PreferenceAction(
                    title = stringResource(R.string.restart_systemui),
                    summary = null,
                    icon = MiuixIcons.Refresh,
                ) {
                    val ok = notificationHelper.restartSystemUI()
                    message = if (ok) {
                        restartRequestedText
                    } else {
                        "重启失败（需要 Root）"
                    }
                }
            }
        }

        // ---- 操作结果提示 ----
        if (message != null) {
            item {
                Text(
                    text = message.orEmpty(),
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onSurfaceSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
            }
        }
    }
}

/** 系统状态类型在 statusFlags / IslandConfig 中的键名（顺序与上方列表一致） */
private val islandConfigKeys = listOf(
    "sys_status_charging",
    "sys_status_hotspot",
    "sys_status_flashlight",
    "sys_status_screen_record",
)
