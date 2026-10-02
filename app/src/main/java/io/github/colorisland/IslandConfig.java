package io.github.colorisland;

import android.content.SharedPreferences;
import io.github.colorisland.island.IslandSystemStatus;
import io.github.libxposed.api.XposedModule;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import io.github.colorisland.util.KResult;
import kotlin.Unit;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* compiled from: IslandConfig.kt */
/* loaded from: classes3.dex */
public final class IslandConfig {
    public static final String CATALOG_KEY_CHANNELS = "channel_catalog";
    private static final int DEFAULT_COMPACT_HEIGHT = 34;
    private static final int DEFAULT_COMPACT_WIDTH = 0;
    private static final int DEFAULT_EXPANDED_HEIGHT = 150;
    public static final String KEY_AUTO_EXPAND = "auto_expand";
    public static final String KEY_AVOID_CUTOUT = "avoid_cutout";
    public static final String KEY_COMPACT_HEIGHT = "compact_height_dp";
    public static final String KEY_COMPACT_WIDTH = "compact_width_dp";
    public static final String KEY_CORNER_RADIUS = "corner_radius_dp";
    public static final String KEY_DEBUG_LOG = "debug_log";
    public static final String KEY_DISABLED_APPS = "disabled_apps";
    public static final String KEY_DISABLED_CHANNELS = "disabled_channels";
    public static final String KEY_DISPLAY_DURATION = "display_duration_ms";
    public static final String KEY_ENABLED = "master_enabled";
    public static final String KEY_EXPANDED_DURATION = "expanded_duration_ms";
    public static final String KEY_EXPANDED_HEIGHT = "expanded_height_dp";
    public static final String KEY_EXPANDED_WIDTH = "expanded_width_dp";
    public static final String KEY_HIDE_GROUP = "hide_group_summary";
    public static final String KEY_HIDE_ONGOING = "hide_ongoing";
    public static final String KEY_HIDE_SYSTEM_CAPSULE = "hide_system_capsule";
    public static final String KEY_ISLAND_ACCENT_COLOR = "island_accent_color";
    public static final String KEY_ISLAND_BG_COLOR = "island_bg_color";
    public static final String KEY_SYS_APP_TAKEOVER = "sys_app_takeover";
    public static final String KEY_SYS_STATUS_CHARGING = "sys_status_charging";
    public static final String KEY_SYS_STATUS_ENABLED = "sys_status_enabled";
    public static final String KEY_SYS_STATUS_FLASHLIGHT = "sys_status_flashlight";
    public static final String KEY_SYS_STATUS_HOTSPOT = "sys_status_hotspot";
    public static final String KEY_SYS_STATUS_PERSISTENT = "sys_status_persistent";
    public static final String KEY_SYS_STATUS_SCREEN_RECORD = "sys_status_screen_record";
    public static final String KEY_Y_OFFSET = "y_offset_dp";
    public static final String MODULE_PACKAGE = "io.github.colorisland";
    public static final String PREFS_NAME = "ColorIslandConfig";
    public static final int Y_OFFSET_MAX_DP = 100;
    public static final int Y_OFFSET_MIN_DP = -100;
    private static volatile int compactWidthDp;
    private static volatile int cornerRadiusDp;
    private static volatile boolean debugLog;
    private static volatile Function0<Unit> onAvoidCutoutChanged;
    private static volatile Function0<Unit> onColorChanged;
    private static volatile Function0<Unit> onSystemStatusDisabled;
    private static volatile Function1<? super Integer, Unit> onYOffsetChanged;
    private static volatile SharedPreferences prefs;
    private static volatile boolean sysAppTakeover;
    private static volatile boolean systemStatusPersistent;
    private static volatile int yOffsetDp;
    public static final IslandConfig INSTANCE = new IslandConfig();
    private static volatile boolean masterEnabled = true;
    private static volatile boolean autoExpand = true;
    private static final int DEFAULT_DISPLAY_DURATION = 5000;
    private static volatile int displayDurationMs = DEFAULT_DISPLAY_DURATION;
    private static final int DEFAULT_EXPANDED_DURATION = 4000;
    private static volatile int expandedDurationMs = DEFAULT_EXPANDED_DURATION;
    private static volatile boolean hideOngoing = true;
    private static volatile boolean hideGroupSummary = true;
    private static boolean avoidCutout = true;
    private static volatile boolean hideSystemCapsule = true;
    private static volatile int compactHeightDp = 34;
    private static final int DEFAULT_EXPANDED_WIDTH = 340;
    private static volatile int expandedWidthDp = DEFAULT_EXPANDED_WIDTH;
    private static volatile int expandedHeightDp = 150;
    public static final int DEFAULT_BG_COLOR = -234024681;
    private static volatile int islandBgColor = DEFAULT_BG_COLOR;
    public static final int DEFAULT_ACCENT_COLOR = -11751600;
    private static volatile int islandAccentColor = DEFAULT_ACCENT_COLOR;
    private static volatile boolean systemStatusEnabled = true;
    private static volatile Map<String, Boolean> statusFlags = MapsKt.emptyMap();
    private static volatile Set<String> disabledApps = SetsKt.emptySet();
    private static volatile Set<String> disabledChannels = SetsKt.emptySet();
    private static final SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: io.github.colorisland.IslandConfig$$ExternalSyntheticLambda0
        @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
        public final void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
            IslandConfig.listener$lambda$0(sharedPreferences, str);
        }
    };
    public static final int $stable = 8;

    /* compiled from: IslandConfig.kt */
    /* loaded from: classes3.dex */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[IslandSystemStatus.Type.values().length];
            try {
                iArr[IslandSystemStatus.Type.FLASHLIGHT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[IslandSystemStatus.Type.CHARGING.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[IslandSystemStatus.Type.SCREEN_RECORD.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                iArr[IslandSystemStatus.Type.HOTSPOT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    private IslandConfig() {
    }

    public static /* synthetic */ boolean isSystemStatusEnabled$default(IslandConfig islandConfig, IslandSystemStatus.Type type, int i, Object obj) {
        if ((i & 1) != 0) {
            type = null;
        }
        return islandConfig.isSystemStatusEnabled(type);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void listener$lambda$0(SharedPreferences sharedPreferences, String str) {
        INSTANCE.reload();
    }

    /**
     * 从（可能为跨进程远程的）SharedPreferences 重载全部配置。
     *
     * 实现依据 v1.0 原始 smali 逐条翻译（IslandConfig.smali reload()V，834 条指令），
     * 读取顺序、默认值、coerce 范围、变化检测回调顺序均与原版一致。
     *
     * 注意：本方法在 SystemUI 进程（Xposed hook 侧）执行，
     * prefs 来自 XposedModule.getRemotePreferences，与 App 进程跨进程同步。
     */
    private final void reload() {
        SharedPreferences sp = prefs;
        if (sp == null) {
            // 远程偏好不可用时保持默认值（masterEnabled 默认 true，不影响开关判断）
            return;
        }
        // ---- 变化检测基准值（必须在读取前保存旧值）----
        boolean oldSystemStatusEnabled = systemStatusEnabled;
        Map<String, Boolean> oldStatusFlags = statusFlags;
        int oldYOffsetDp = yOffsetDp;
        int oldBgColor = islandBgColor;
        int oldAccentColor = islandAccentColor;
        boolean oldAvoidCutout = avoidCutout;
        // ---- 读取全部配置（try 范围与原版一致：仅包住读取，异常静默吞掉）----
        try {
            masterEnabled = sp.getBoolean(KEY_ENABLED, true);
            autoExpand = sp.getBoolean(KEY_AUTO_EXPAND, true);
            displayDurationMs = Math.max(1500, Math.min(30000,
                    sp.getInt(KEY_DISPLAY_DURATION, DEFAULT_DISPLAY_DURATION)));
            expandedDurationMs = Math.max(1000, Math.min(20000,
                    sp.getInt(KEY_EXPANDED_DURATION, DEFAULT_EXPANDED_DURATION)));
            hideOngoing = sp.getBoolean(KEY_HIDE_ONGOING, true);
            hideGroupSummary = sp.getBoolean(KEY_HIDE_GROUP, true);
            yOffsetDp = Math.max(Y_OFFSET_MIN_DP, Math.min(Y_OFFSET_MAX_DP,
                    sp.getInt(KEY_Y_OFFSET, 0)));
            debugLog = sp.getBoolean(KEY_DEBUG_LOG, false);
            avoidCutout = sp.getBoolean(KEY_AVOID_CUTOUT, true);
            hideSystemCapsule = sp.getBoolean(KEY_HIDE_SYSTEM_CAPSULE, true);
            compactWidthDp = Math.max(0, Math.min(400,
                    sp.getInt(KEY_COMPACT_WIDTH, DEFAULT_COMPACT_WIDTH)));
            compactHeightDp = Math.max(20, Math.min(90,
                    sp.getInt(KEY_COMPACT_HEIGHT, DEFAULT_COMPACT_HEIGHT)));
            expandedWidthDp = Math.max(160, Math.min(460,
                    sp.getInt(KEY_EXPANDED_WIDTH, DEFAULT_EXPANDED_WIDTH)));
            expandedHeightDp = Math.max(60, Math.min(640,
                    sp.getInt(KEY_EXPANDED_HEIGHT, DEFAULT_EXPANDED_HEIGHT)));
            cornerRadiusDp = Math.max(0, Math.min(45,
                    sp.getInt(KEY_CORNER_RADIUS, 0)));
            islandBgColor = sp.getInt(KEY_ISLAND_BG_COLOR, DEFAULT_BG_COLOR);
            islandAccentColor = sp.getInt(KEY_ISLAND_ACCENT_COLOR, DEFAULT_ACCENT_COLOR);
            systemStatusEnabled = sp.getBoolean(KEY_SYS_STATUS_ENABLED, true);
            // 四个系统状态开关（key 顺序与原版一致）
            Map<String, Boolean> flags = new LinkedHashMap<>();
            flags.put(KEY_SYS_STATUS_FLASHLIGHT, sp.getBoolean(KEY_SYS_STATUS_FLASHLIGHT, true));
            flags.put(KEY_SYS_STATUS_CHARGING, sp.getBoolean(KEY_SYS_STATUS_CHARGING, true));
            flags.put(KEY_SYS_STATUS_SCREEN_RECORD, sp.getBoolean(KEY_SYS_STATUS_SCREEN_RECORD, true));
            flags.put(KEY_SYS_STATUS_HOTSPOT, sp.getBoolean(KEY_SYS_STATUS_HOTSPOT, true));
            statusFlags = flags;
            systemStatusPersistent = sp.getBoolean(KEY_SYS_STATUS_PERSISTENT, false);
            sysAppTakeover = sp.getBoolean(KEY_SYS_APP_TAKEOVER, false);
            disabledApps = splitCsvToSet(sp.getString(KEY_DISABLED_APPS, ""));
            disabledChannels = splitCsvToSet(sp.getString(KEY_DISABLED_CHANNELS, ""));
        } catch (Throwable th) {
            // 与原版行为一致：读取失败时保留已读入的值，不中断初始化
        }
        // ---- 变化检测与回调（顺序与原版一致：系统状态禁用 → Y偏移 → 颜色 → 避让）----
        // 1) 系统状态被禁用：总开关关闭，或任一状态由启用（true/无记录）变为禁用（false）
        boolean systemStatusJustDisabled = !systemStatusEnabled && oldSystemStatusEnabled;
        if (!systemStatusJustDisabled && !statusFlags.isEmpty()) {
            for (Map.Entry<String, Boolean> entry : statusFlags.entrySet()) {
                Boolean newValue = entry.getValue();
                if (newValue != null && !newValue && !Boolean.FALSE.equals(oldStatusFlags.get(entry.getKey()))) {
                    systemStatusJustDisabled = true;
                    break;
                }
            }
        }
        if (systemStatusJustDisabled) {
            Function0<Unit> cb = onSystemStatusDisabled;
            if (cb != null) {
                cb.invoke();
            }
        }
        // 2) Y 偏移变化
        if (yOffsetDp != oldYOffsetDp) {
            Function1<? super Integer, Unit> cb = onYOffsetChanged;
            if (cb != null) {
                cb.invoke(yOffsetDp);
            }
        }
        // 3) 背景色/强调色变化
        if (islandBgColor != oldBgColor || islandAccentColor != oldAccentColor) {
            Function0<Unit> cb = onColorChanged;
            if (cb != null) {
                cb.invoke();
            }
        }
        // 4) 避让前置摄像头开关变化
        if (avoidCutout != oldAvoidCutout) {
            Function0<Unit> cb = onAvoidCutoutChanged;
            if (cb != null) {
                cb.invoke();
            }
        }
    }

    /** 逗号分隔 CSV → 去空白、去空项的集合（对应原版 split(',').map{trim}.filter{isNotEmpty}.toSet()） */
    private static Set<String> splitCsvToSet(String raw) {
        Set<String> result = new LinkedHashSet<>();
        if (raw == null) {
            return result;
        }
        for (String part : raw.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    public final boolean getAutoExpand() {
        return autoExpand;
    }

    public final boolean getAvoidCutout() {
        return avoidCutout;
    }

    public final int getCompactHeightDp() {
        return compactHeightDp;
    }

    public final int getCompactWidthDp() {
        return compactWidthDp;
    }

    public final int getCornerRadiusDp() {
        return cornerRadiusDp;
    }

    public final boolean getDebugLog() {
        return debugLog;
    }

    public final int getDisplayDurationMs() {
        return displayDurationMs;
    }

    public final int getExpandedDurationMs() {
        return expandedDurationMs;
    }

    public final int getExpandedHeightDp() {
        return expandedHeightDp;
    }

    public final int getExpandedWidthDp() {
        return expandedWidthDp;
    }

    public final boolean getHideGroupSummary() {
        return hideGroupSummary;
    }

    public final boolean getHideOngoing() {
        return hideOngoing;
    }

    public final boolean getHideSystemCapsule() {
        return hideSystemCapsule;
    }

    public final int getIslandAccentColor() {
        return islandAccentColor;
    }

    public final int getIslandBgColor() {
        return islandBgColor;
    }

    public final boolean getMasterEnabled() {
        return masterEnabled;
    }

    public final Function0<Unit> getOnAvoidCutoutChanged() {
        return onAvoidCutoutChanged;
    }

    public final Function0<Unit> getOnColorChanged() {
        return onColorChanged;
    }

    public final Function0<Unit> getOnSystemStatusDisabled() {
        return onSystemStatusDisabled;
    }

    public final Function1<? super Integer, Unit> getOnYOffsetChanged() {
        return onYOffsetChanged;
    }

    public final Map<String, Boolean> getStatusFlags() {
        return statusFlags;
    }

    public final boolean getSysAppTakeover() {
        return sysAppTakeover;
    }

    public final boolean getSystemStatusEnabled() {
        return systemStatusEnabled;
    }

    public final boolean getSystemStatusPersistent() {
        return systemStatusPersistent;
    }

    public final int getYOffsetDp() {
        return yOffsetDp;
    }

    public final synchronized void init(XposedModule module) {
        Object m5221constructorimpl;
        Intrinsics.checkNotNullParameter(module, "module");
        if (prefs != null) {
            return;
        }
        try {
            IslandConfig islandConfig = this;
            m5221constructorimpl = KResult.success(module.getRemotePreferences(PREFS_NAME));
        } catch (Throwable th) {
            m5221constructorimpl = KResult.success(KResult.createFailure(th));
        }
        Throwable m5224exceptionOrNullimpl = KResult.exceptionOrNullimpl(m5221constructorimpl);
        if (m5224exceptionOrNullimpl != null) {
            module.log(5, "ColorIsland", "remote prefs unavailable: " + m5224exceptionOrNullimpl.getMessage());
            m5221constructorimpl = null;
        }
        prefs = (SharedPreferences) m5221constructorimpl;
        reload();
        SharedPreferences sharedPreferences = prefs;
        if (sharedPreferences != null) {
            sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        }
    }

    public final boolean isAppEnabled(String pkg) {
        Intrinsics.checkNotNullParameter(pkg, "pkg");
        return !disabledApps.contains(pkg);
    }

    public final boolean isChannelEnabled(String pkg, String channelId) {
        Intrinsics.checkNotNullParameter(pkg, "pkg");
        String str = channelId;
        if (str == null || StringsKt.isBlank(str)) {
            return true;
        }
        return !disabledChannels.contains(pkg + ":" + channelId);
    }

    public final boolean isSystemStatusEnabled(IslandSystemStatus.Type type) {
        String str;
        if (!systemStatusEnabled) {
            return false;
        }
        if (type == null) {
            return true;
        }
        int i = WhenMappings.$EnumSwitchMapping$0[type.ordinal()];
        if (i == 1) {
            str = "sys_status_flashlight";
        } else if (i == 2) {
            str = "sys_status_charging";
        } else if (i == 3) {
            str = "sys_status_screen_record";
        } else {
            if (i != 4) {
                throw new NoWhenBranchMatchedException();
            }
            str = "sys_status_hotspot";
        }
        Boolean bool = statusFlags.get(str);
        if (bool != null) {
            return bool.booleanValue();
        }
        return true;
    }

    public final void setDebugLog(boolean z) {
        debugLog = z;
    }

    public final void setOnAvoidCutoutChanged(Function0<Unit> function0) {
        onAvoidCutoutChanged = function0;
    }

    public final void setOnColorChanged(Function0<Unit> function0) {
        onColorChanged = function0;
    }

    public final void setOnSystemStatusDisabled(Function0<Unit> function0) {
        onSystemStatusDisabled = function0;
    }

    public final void setOnYOffsetChanged(Function1<? super Integer, Unit> function1) {
        onYOffsetChanged = function1;
    }

    public final void setStatusFlags(Map<String, Boolean> map) {
        Intrinsics.checkNotNullParameter(map, "setter");
        statusFlags = map;
    }
}
