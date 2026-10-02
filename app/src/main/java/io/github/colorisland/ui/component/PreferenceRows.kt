package io.github.colorisland.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.remember
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.icon.extended.Ok
import top.yukonga.miuix.kmp.preference.SliderPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.preference.WindowDropdownPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Miuix 风格设置行封装。
 *
 * 这一层是对 miuix 0.9.4 preference 组件的薄封装，目的：
 * 1. 统一图标插槽（startAction）与内边距，保证全应用视觉一致；
 * 2. 页面代码只关心业务语义（标题/摘要/值/回调），不接触 miuix 细节，
 *    将来升级 miuix 版本只需调整这一层。
 */

/** 开关设置行 */
@Composable
fun PreferenceSwitch(
    title: String,
    summary: String?,
    icon: ImageVector?,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
) {
    SwitchPreference(
        title = title,
        summary = summary,
        checked = checked,
        enabled = enabled,
        startAction = icon?.let { image -> { Icon(image, title) } },
        insideMargin = SettingsItemMargin,
        onCheckedChange = onCheckedChange,
    )
}

/** 滑块设置行（整数步进，valueText 由调用方格式化） */
@Composable
fun PreferenceSlider(
    title: String,
    summary: String? = null,
    icon: ImageVector? = null,
    value: Float,
    valueText: String,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit = {},
) {
    SliderPreference(
        title = title,
        summary = summary,
        value = value,
        valueText = valueText,
        valueRange = valueRange,
        steps = steps,
        startAction = icon?.let { image -> { Icon(image, title) } },
        insideMargin = SettingsItemMargin,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
    )
}

/** 下拉选择设置行 */
@Composable
fun PreferenceDropdown(
    title: String,
    summary: String?,
    icon: ImageVector?,
    items: List<String>,
    selectedIndex: Int,
    enabled: Boolean = true,
    onSelectedIndexChange: (Int) -> Unit,
) {
    WindowDropdownPreference(
        title = title,
        summary = summary,
        items = items,
        selectedIndex = selectedIndex,
        enabled = enabled,
        startAction = icon?.let { image -> { Icon(image, title) } },
        insideMargin = SettingsItemMargin,
        onSelectedIndexChange = onSelectedIndexChange,
    )
}

/**
 * 内联预设色板设置行（配色选择专用）。
 *
 * 背景：miuix 0.9.4 的 WindowDropdownPreference 弹出 ListPopupLayout 时，
 * 弹窗是独立窗口，其内容视图沿视图树上溯找不到 Activity 设置的
 * NavigationEventDispatcherOwner，必然触发 IllegalStateException 崩溃。
 * 因此配色选择改为完全内联的色板：不创建任何独立弹窗，从根上规避。
 *
 * 视觉规范（对齐 miuix 设计语言）：
 * - 标题行与其他设置项同款（BasicComponent），右侧圆点展示当前颜色；
 * - 色板置于淡色圆角容器内（16dp 圆角，与 miuix 卡片语言一致）；
 * - 选中态：主题 primary 描边 + 自适应颜色对勾（miuix 经典选中反馈）；
 * - 对勾颜色按色块亮度自适应，保证浅色块上也清晰。
 */
@Composable
fun PreferenceColorPresets(
    title: String,
    icon: ImageVector?,
    presets: List<Pair<String, Int>>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
) {
    // 当前颜色：下标越界时回退到第一个预设，保证圆点始终有有效颜色
    val currentColor = presets.getOrNull(selectedIndex)?.second
        ?: presets.firstOrNull()?.second ?: 0
    Column {
        // 标题行：右侧圆点展示当前颜色（本行不可点击，选色请点下方色块）
        BasicComponent(
            title = title,
            summary = null,
            startAction = icon?.let { image -> { Icon(image, title) } },
            endActions = {
                Box(
                    Modifier
                        .size(24.dp)
                        .background(Color(currentColor), CircleShape)
                        .border(
                            1.dp,
                            MiuixTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                            CircleShape,
                        )
                )
            },
            insideMargin = SettingsItemMargin,
            onClick = {},
        )
        // 色板容器：淡色圆角背景，让一组色块有"嵌入卡片"的分组感
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    MiuixTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f)
                )
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            presets.forEachIndexed { index, (name, colorValue) ->
                val selected = index == selectedIndex
                val swatchColor = Color(colorValue)
                // 对勾颜色自适应：亮色块用深色对勾，暗色块用白色对勾
                val checkTint = if (swatchColor.luminance() > 0.5f) {
                    Color.Black.copy(alpha = 0.65f)
                } else {
                    Color.White
                }
                // 每个色块独立的 interactionSource，避免共享导致的状态残留
                val interactionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        // 必须 clip：否则点击水波纹按矩形 bounds 绘制，出现"方块阴影"
                        .clip(CircleShape)
                        .background(swatchColor)
                        .then(
                            if (selected) {
                                Modifier.border(
                                    2.dp,
                                    MiuixTheme.colorScheme.primary,
                                    CircleShape,
                                )
                            } else {
                                Modifier
                            }
                        )
                        // indication = null：选中态已有对勾+描边反馈，不需要水波纹
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onSelectedIndexChange(index) },
                        )
                        .semantics { contentDescription = name },
                    contentAlignment = Alignment.Center,
                ) {
                    // 仅选中项显示对勾（miuix 经典选中反馈）
                    if (selected) {
                        Icon(
                            imageVector = MiuixIcons.Ok,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = checkTint,
                        )
                    }
                }
            }
        }
    }
}

/** 点击动作行（右侧箭头，用于"测试通知"等一次性操作） */
@Composable
fun PreferenceAction(
    title: String,
    summary: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    BasicComponent(
        title = title,
        summary = summary,
        enabled = enabled,
        startAction = icon?.let { image -> { Icon(image, title) } },
        endActions = {
            // 右侧箭头图标，语义为"可点击"
            Icon(MiuixIcons.Basic.ArrowRight, title)
        },
        insideMargin = SettingsItemMargin,
        onClick = onClick,
    )
}

/** 设置项统一的内边距（与 HyperIsland 的 SettingsItemMargin 对齐） */
val SettingsItemMargin: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
