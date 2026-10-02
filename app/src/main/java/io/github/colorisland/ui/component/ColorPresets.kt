package io.github.colorisland.ui.component

import io.github.colorisland.R

/**
 * 灵动岛配色预设。
 *
 * 默认色即 IslandConfig.DEFAULT_ACCENT_COLOR / DEFAULT_BG_COLOR。
 * 修复记录：v1.0 原版"苹果绿"误用了与"默认"相同的色值（-11751600），
 * 导致 ColorPaletteRow 选中态永远落在下标 0，绿色无法选中；
 * 此处将"苹果绿"修正为 iOS systemGreen（0xFF34C759），
 * "默认"色值保持不变，不影响存量用户配置。
 */
object ColorPresets {

    /** 强调色预设：(字符串资源, ARGB 颜色值) */
    val ACCENT: List<Pair<Int, Int>> = listOf(
        R.string.color_preset_default to -11751600,
        R.string.color_preset_green to -13319311,
        R.string.color_preset_blue to -16087809,
        R.string.color_preset_cyan to -13455898,
        R.string.color_preset_purple to -4236558,
        R.string.color_preset_orange to -24822,
        R.string.color_preset_pink to -51361,
    )

    /** 背景色预设：(字符串资源, ARGB 颜色值) */
    val BACKGROUND: List<Pair<Int, Int>> = listOf(
        R.string.color_preset_default to -234024681,
        R.string.color_preset_space_black to -234881024,
        R.string.color_preset_pure_black to -16777216,
        R.string.color_preset_deep_blue to -233821629,
        R.string.color_preset_deep_purple to -232907957,
        R.string.color_preset_system_gray to -233038818,
        R.string.color_preset_white to -218959113,
    )

    /** 按当前颜色值找预设下标；未命中返回 0（默认） */
    fun indexOfAccent(color: Int): Int =
        ACCENT.indexOfFirst { it.second == color }.takeIf { it >= 0 } ?: 0

    fun indexOfBackground(color: Int): Int =
        BACKGROUND.indexOfFirst { it.second == color }.takeIf { it >= 0 } ?: 0
}
