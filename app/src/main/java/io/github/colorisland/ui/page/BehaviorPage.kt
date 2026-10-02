package io.github.colorisland.ui.page

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.colorisland.R
import io.github.colorisland.data.PrefsRepository
import io.github.colorisland.ui.component.CollapsingPage
import io.github.colorisland.ui.component.ColorPresets
import io.github.colorisland.ui.component.PreferenceAction
import io.github.colorisland.ui.component.PreferenceColorPresets
import io.github.colorisland.ui.component.PreferenceSlider
import io.github.colorisland.ui.component.PreferenceSwitch
import io.github.colorisland.ui.component.SectionTitle
import io.github.colorisland.ui.component.SettingsCard
import io.github.colorisland.ui.component.SettingsItemMargin
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Scan
import top.yukonga.miuix.kmp.icon.extended.Close
import top.yukonga.miuix.kmp.icon.extended.Copy
import top.yukonga.miuix.kmp.icon.extended.VerticalSplit
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.Stopwatch
import top.yukonga.miuix.kmp.icon.extended.Info
import top.yukonga.miuix.kmp.icon.basic.ArrowUpDown
import top.yukonga.miuix.kmp.icon.extended.Theme
import top.yukonga.miuix.kmp.icon.extended.Play
import top.yukonga.miuix.kmp.icon.extended.Refresh
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.icon.extended.Timer
import top.yukonga.miuix.kmp.icon.extended.ExpandMore
import top.yukonga.miuix.kmp.icon.extended.HorizontalSplit
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 行为与外观页：时长、尺寸、颜色、行为开关。
 *
 * 与 v1.0 BehaviorScreen 的对应关系：
 * - 所有滑块/开关均通过 PrefsRepository 读写，与 Hook 进程共享同一份配置；
 * - "隐藏系统胶囊"开关在 PrefsRepository 中没有封装（仓库未暴露该键），
 *   这里直接写 SharedPreferences（键名与 IslandConfig.KEY_HIDE_SYSTEM_CAPSULE 一致），
 *   保证该功能不丢失；
 * - 颜色预设数值与 v1.0 完全一致（见 ColorPresets）。
 *
 * @param repo         配置仓库
 * @param prefsVersion 配置版本号，变化时整页刷新
 */
@Composable
fun BehaviorPage(
    repo: PrefsRepository,
    prefsVersion: Int,
) {
    // 隐藏系统胶囊：直接读写 prefs（仓库未封装此键）
    var hideSystemCapsule by remember {
        mutableStateOf(repo.prefs.getBoolean("hide_system_capsule", false))
    }

    CollapsingPage(
        title = stringResource(R.string.behavior_title),
        subtitle = "",
    ) {
        // ========== 显示时长 ==========
        item {
            SectionTitle(stringResource(R.string.behavior_duration_section))
            SettingsCard {
                PreferenceSlider(
                    title = stringResource(R.string.behavior_duration),
                    summary = stringResource(R.string.display_duration_desc),
                    icon = MiuixIcons.Timer,
                    value = repo.displayDurationMs.toFloat(),
                    valueText = "%.1f 秒".format(repo.displayDurationMs / 1000f),
                    valueRange = 1000f..15000f,
                    steps = 27,
                    onValueChange = { repo.updateDisplayDurationMs(it.toInt()) },
                )
                PreferenceSlider(
                    title = stringResource(R.string.behavior_expanded_duration),
                    summary = stringResource(R.string.expanded_duration_desc),
                    icon = MiuixIcons.Stopwatch,
                    value = repo.expandedDurationMs.toFloat(),
                    valueText = "%.1f 秒".format(repo.expandedDurationMs / 1000f),
                    valueRange = 2000f..30000f,
                    steps = 55,
                    onValueChange = { repo.updateExpandedDurationMs(it.toInt()) },
                )
            }
        }

        // ========== 尺寸 ==========
        item {
            SectionTitle(stringResource(R.string.behavior_size_section))
            SettingsCard {
                PreferenceSlider(
                    title = stringResource(R.string.compact_width),
                    summary = stringResource(R.string.compact_width_desc),
                    icon = MiuixIcons.HorizontalSplit,
                    value = repo.compactWidthDp.toFloat(),
                    valueText = if (repo.compactWidthDp == 0) "自动" else "${repo.compactWidthDp} dp",
                    valueRange = 0f..300f,
                    steps = 149,
                    onValueChange = { repo.updateCompactWidthDp(it.toInt()) },
                )
                PreferenceSlider(
                    title = stringResource(R.string.compact_height),
                    summary = stringResource(R.string.compact_height_desc),
                    icon = MiuixIcons.VerticalSplit,
                    value = repo.compactHeightDp.toFloat(),
                    valueText = "${repo.compactHeightDp} dp",
                    valueRange = 24f..80f,
                    steps = 55,
                    onValueChange = { repo.updateCompactHeightDp(it.toInt()) },
                )
                PreferenceSlider(
                    title = stringResource(R.string.expanded_width),
                    summary = stringResource(R.string.expanded_width_desc),
                    icon = MiuixIcons.Stopwatch,
                    value = repo.expandedWidthDp.toFloat(),
                    valueText = if (repo.expandedWidthDp == 0) "自动" else "${repo.expandedWidthDp} dp",
                    valueRange = 0f..500f,
                    steps = 99,
                    onValueChange = { repo.updateExpandedWidthDp(it.toInt()) },
                )
                PreferenceSlider(
                    title = stringResource(R.string.expanded_height),
                    summary = stringResource(R.string.expanded_height_desc),
                    icon = MiuixIcons.ExpandMore,
                    value = repo.expandedHeightDp.toFloat(),
                    valueText = "${repo.expandedHeightDp} dp",
                    valueRange = 60f..400f,
                    steps = 67,
                    onValueChange = { repo.updateExpandedHeightDp(it.toInt()) },
                )
                PreferenceSlider(
                    title = stringResource(R.string.corner_radius),
                    summary = stringResource(R.string.corner_radius_desc),
                    icon = MiuixIcons.VerticalSplit,
                    value = repo.cornerRadiusDp.toFloat(),
                    valueText = "${repo.cornerRadiusDp} dp",
                    valueRange = 0f..36f,
                    steps = 35,
                    onValueChange = { repo.updateCornerRadiusDp(it.toInt()) },
                )
                PreferenceSlider(
                    title = stringResource(R.string.behavior_y_offset),
                    summary = stringResource(R.string.y_offset_desc),
                    icon = MiuixIcons.Basic.ArrowUpDown,
                    value = repo.yOffsetDp.toFloat(),
                    valueText = "${repo.yOffsetDp} dp",
                    valueRange = -100f..100f,
                    steps = 199,
                    onValueChange = { repo.updateYOffsetDp(it.toInt()) },
                )
            }
        }

        // ========== 颜色 ==========
        item {
            SectionTitle(stringResource(R.string.behavior_color_section))
            SettingsCard {
                // 强调色预设色板（内联，规避 WindowDropdownPreference 弹窗崩溃）
                PreferenceColorPresets(
                    title = stringResource(R.string.island_accent_color),
                    icon = MiuixIcons.Theme,
                    presets = ColorPresets.ACCENT.map { (nameRes, color) ->
                        stringResource(nameRes) to color
                    },
                    selectedIndex = ColorPresets.indexOfAccent(repo.islandAccentColor),
                    onSelectedIndexChange = { index ->
                        ColorPresets.ACCENT.getOrNull(index)?.let { (_, color) ->
                            repo.updateIslandAccentColor(color)
                        }
                    },
                )
                // 背景色预设色板（内联，规避 WindowDropdownPreference 弹窗崩溃）
                PreferenceColorPresets(
                    title = stringResource(R.string.island_bg_color),
                    icon = MiuixIcons.Copy,
                    presets = ColorPresets.BACKGROUND.map { (nameRes, color) ->
                        stringResource(nameRes) to color
                    },
                    selectedIndex = ColorPresets.indexOfBackground(repo.islandBgColor),
                    onSelectedIndexChange = { index ->
                        ColorPresets.BACKGROUND.getOrNull(index)?.let { (_, color) ->
                            repo.updateIslandBgColor(color)
                        }
                    },
                )
                // 恢复默认配色
                PreferenceAction(
                    title = stringResource(R.string.color_reset),
                    summary = null,
                    icon = MiuixIcons.Refresh,
                ) {
                    repo.updateIslandAccentColor(-11751600)
                    repo.updateIslandBgColor(-234024681)
                }
            }
        }

        // ========== 行为 ==========
        item {
            SectionTitle(stringResource(R.string.behavior_behavior_section))
            SettingsCard {
                PreferenceSwitch(
                    title = stringResource(R.string.behavior_auto_expand),
                    summary = stringResource(R.string.behavior_auto_expand_desc),
                    icon = MiuixIcons.Stopwatch,
                    checked = repo.autoExpand,
                    onCheckedChange = { repo.updateAutoExpand(it) },
                )
                PreferenceSwitch(
                    title = stringResource(R.string.behavior_avoid_cutout),
                    summary = stringResource(R.string.behavior_avoid_cutout_desc),
                    icon = MiuixIcons.Scan,
                    checked = repo.avoidCutout,
                    onCheckedChange = { repo.updateAvoidCutout(it) },
                )
                PreferenceSwitch(
                    title = stringResource(R.string.behavior_hide_group),
                    summary = stringResource(R.string.behavior_hide_group_desc),
                    icon = MiuixIcons.Copy,
                    checked = repo.hideGroupSummary,
                    onCheckedChange = { repo.updateHideGroupSummary(it) },
                )
                PreferenceSwitch(
                    title = stringResource(R.string.behavior_hide_ongoing),
                    summary = stringResource(R.string.behavior_hide_ongoing_desc),
                    icon = MiuixIcons.Close,
                    checked = repo.hideOngoing,
                    onCheckedChange = { repo.updateHideOngoing(it) },
                )
                // 隐藏系统胶囊：直接写 prefs（仓库未封装）
                PreferenceSwitch(
                    title = stringResource(R.string.hide_system_capsule),
                    summary = stringResource(R.string.hide_system_capsule_desc),
                    icon = MiuixIcons.Delete,
                    checked = hideSystemCapsule,
                    onCheckedChange = {
                        hideSystemCapsule = it
                        repo.prefs.edit().putBoolean("hide_system_capsule", it).apply()
                    },
                )
                PreferenceSwitch(
                    title = stringResource(R.string.sys_app_takeover),
                    summary = stringResource(R.string.sys_app_takeover_desc),
                    icon = MiuixIcons.Settings,
                    checked = repo.sysAppTakeover,
                    onCheckedChange = { repo.updateSysAppTakeover(it) },
                )
                PreferenceSwitch(
                    title = stringResource(R.string.behavior_debug_log),
                    summary = stringResource(R.string.behavior_debug_log_desc),
                    icon = MiuixIcons.Info,
                    checked = repo.debugLog,
                    onCheckedChange = { repo.updateDebugLog(it) },
                )
            }
        }

        // ========== 关于 ==========
        item {
            SectionTitle(stringResource(R.string.about))
            SettingsCard {
                Text(
                    text = stringResource(R.string.about_desc),
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceSecondary,
                    modifier = Modifier.padding(SettingsItemMargin),
                )
            }
        }
    }
}
