package io.github.colorisland.ui.theme

import androidx.compose.runtime.Composable
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController

/**
 * ColorIsland 应用主题。
 *
 * 设计说明：
 * - 直接复用 Miuix 0.9.4 的默认配色体系（ThemeController 无参构造即跟随系统深浅色），
 *   与 HyperIsland 的设计语言保持一致：大圆角卡片、细腻分割线、无衬线排版。
 * - 灵动岛本体是系统级悬浮窗，其配色由 IslandConfig 独立管理，
 *   与应用 UI 主题解耦，避免互相影响。
 */
@Composable
fun ColorIslandTheme(
    content: @Composable () -> Unit,
) {
    // ThemeController() 默认 ColorSchemeMode.System，随系统深浅色切换
    MiuixTheme(
        controller = ThemeController(),
        content = content,
    )
}
