package io.github.colorisland.ui.component

import io.github.colorisland.R
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * ColorPresets 配色预设测试。
 *
 * 回归背景（v1.0 缺陷修复）：
 * 原版"苹果绿"误用与"默认"相同的色值 -11751600，导致 ColorPaletteRow
 * 选中态永远落在下标 0，用户点击"苹果绿"无任何变化（"第二个绿色选不了"）。
 * 修复后"苹果绿"为独立色值 -13319311（0xFF34C759 iOS systemGreen）。
 */
class ColorPresetsTest {

    @Test
    fun `强调色默认与苹果绿色值必须不同`() {
        val defaultColor = ColorPresets.ACCENT[0].second
        val greenColor = ColorPresets.ACCENT[1].second
        assertNotEquals(
            defaultColor,
            greenColor,
            "默认与苹果绿色值相同会导致 indexOfFirst 永远命中下标0，绿色无法选中",
        )
    }

    @Test
    fun `苹果绿色值为修复后的独立值`() {
        assertEquals(-13319311, ColorPresets.ACCENT[1].second)
        assertEquals(R.string.color_preset_green, ColorPresets.ACCENT[1].first)
    }

    @Test
    fun `默认色值保持原版不变`() {
        assertEquals(-11751600, ColorPresets.ACCENT[0].second)
        assertEquals(R.string.color_preset_default, ColorPresets.ACCENT[0].first)
    }

    @Test
    fun `indexOfAccent 默认色命中下标0`() {
        assertEquals(0, ColorPresets.indexOfAccent(-11751600))
    }

    @Test
    fun `indexOfAccent 苹果绿命中下标1_核心回归`() {
        // 修复前：green 与 default 同值，此断言返回 0（bug 根因）
        assertEquals(1, ColorPresets.indexOfAccent(-13319311))
    }

    @Test
    fun `indexOfAccent 每个预设色值都能命中自身下标`() {
        ColorPresets.ACCENT.forEachIndexed { index, (_, color) ->
            assertEquals(
                index,
                ColorPresets.indexOfAccent(color),
                "预设[$index] 色值 $color 无法命中自身下标",
            )
        }
    }

    @Test
    fun `indexOfAccent 未命中色值回退下标0`() {
        assertEquals(0, ColorPresets.indexOfAccent(-999999))
        assertEquals(0, ColorPresets.indexOfAccent(0))
    }

    @Test
    fun `indexOfBackground 每个预设色值都能命中自身下标`() {
        ColorPresets.BACKGROUND.forEachIndexed { index, (_, color) ->
            assertEquals(
                index,
                ColorPresets.indexOfBackground(color),
                "背景预设[$index] 色值 $color 无法命中自身下标",
            )
        }
    }

    @Test
    fun `强调色预设数量为7且无重复色值`() {
        assertEquals(7, ColorPresets.ACCENT.size)
        val colors = ColorPresets.ACCENT.map { it.second }
        assertEquals(colors.size, colors.toSet().size, "强调色预设存在重复色值")
    }

    @Test
    fun `背景色预设数量为7且无重复色值`() {
        assertEquals(7, ColorPresets.BACKGROUND.size)
        val colors = ColorPresets.BACKGROUND.map { it.second }
        assertEquals(colors.size, colors.toSet().size, "背景色预设存在重复色值")
    }

    @Test
    fun `恢复默认配色按钮使用的色值与预设默认一致`() {
        // BehaviorPage 中"恢复默认配色"硬编码的色值必须与预设[0]一致
        assertTrue(ColorPresets.ACCENT[0].second == -11751600)
        assertTrue(ColorPresets.BACKGROUND[0].second == -234024681)
    }
}
