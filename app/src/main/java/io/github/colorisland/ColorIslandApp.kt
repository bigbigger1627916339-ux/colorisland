package io.github.colorisland

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.colorisland.data.PrefsRepository
import io.github.colorisland.ui.page.AppsPage
import io.github.colorisland.ui.page.BehaviorPage
import io.github.colorisland.ui.page.HomePage
import io.github.colorisland.ui.theme.ColorIslandTheme
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.GridView
import top.yukonga.miuix.kmp.icon.extended.Home
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * ColorIsland 应用外壳：Miuix 风格底部导航 + 三个页面。
 *
 * 设计说明（对齐 HyperIsland 的 Miuix 设计模式）：
 * - 底部导航使用 miuix 0.9.4 的 NavigationBar / NavigationItem；
 * - 三个页面各自持有 CollapsingPage（大标题顶栏），切换时独立滚动；
 * - 配置变化刷新：PrefsRepository 的 getter 是惰性 StateFlow，
 *   直接读取即可在配置写入后自动重组；prefsVersion 作为兜底计数器，
 *   监听 SharedPreferences 变化递增，确保列表类页面（应用/渠道）也能刷新。
 *
 * @param repo 配置仓库（由 ConfigSyncApp 提供，生命周期与应用一致）
 */
@Composable
fun ColorIslandApp(repo: PrefsRepository) {
    // 当前选中的 Tab：0=首页 1=应用 2=行为
    var selectedTab by remember { mutableIntStateOf(0) }
    // 配置版本号：任意配置键变化时 +1，触发整页刷新
    var prefsVersion by remember { mutableIntStateOf(0) }

    // 监听 SharedPreferences 变化，递增版本号
    val listener = remember {
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> prefsVersion++ }
    }
    DisposableEffect(repo) {
        repo.prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            repo.prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    ColorIslandTheme {
        Surface(color = MiuixTheme.colorScheme.background) {
            Box(modifier = Modifier.fillMaxSize()) {
                // 页面内容区（为底部导航留出空间）
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                ) {
                    when (selectedTab) {
                        0 -> HomePage(repo = repo, prefsVersion = prefsVersion)
                        1 -> AppsPage(repo = repo, prefsVersion = prefsVersion)
                        else -> BehaviorPage(repo = repo, prefsVersion = prefsVersion)
                    }
                }

                // 底部导航栏（悬浮式，Miuix 标志性设计）
                NavigationBar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = MiuixIcons.Home,
                        label = stringResource(R.string.tab_home),
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = MiuixIcons.GridView,
                        label = stringResource(R.string.tab_apps),
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = MiuixIcons.Settings,
                        label = stringResource(R.string.tab_behavior),
                    )
                }
            }
        }
    }
}
