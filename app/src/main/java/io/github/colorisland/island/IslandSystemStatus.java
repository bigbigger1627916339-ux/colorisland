package io.github.colorisland.island;

import android.graphics.drawable.Drawable;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: IslandSystemStatus.kt */
/* loaded from: classes3.dex */
public final /* data */ class IslandSystemStatus {
    public static final long TRANSIENT_DURATION_MS = 3500;
    private final String connInfo;
    private final long durationMs;
    private final Drawable icon;
    private final boolean persistent;
    private final String sourceLabel;
    private final String text;
    private final String title;
    private final Type type;

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    public static final int $stable = 8;

    /* compiled from: IslandSystemStatus.kt */
    /* loaded from: classes3.dex */
    public static final class Companion {

        /* compiled from: IslandSystemStatus.kt */
        /* loaded from: classes3.dex */
        public /* synthetic */ class WhenMappings {
            public static final /* synthetic */ int[] $EnumSwitchMapping$0;

            static {
                int[] iArr = new int[Type.values().length];
                try {
                    iArr[Type.SCREEN_RECORD.ordinal()] = 1;
                } catch (NoSuchFieldError unused) {
                }
                try {
                    iArr[Type.HOTSPOT.ordinal()] = 2;
                } catch (NoSuchFieldError unused) {
                }
                try {
                    iArr[Type.CHARGING.ordinal()] = 3;
                } catch (NoSuchFieldError unused) {
                }
                try {
                    iArr[Type.FLASHLIGHT.ordinal()] = 4;
                } catch (NoSuchFieldError unused) {
                }
                $EnumSwitchMapping$0 = iArr;
            }
        }

        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final int priorityOf(Type type) {
            Intrinsics.checkNotNullParameter(type, "type");
            int i = WhenMappings.$EnumSwitchMapping$0[type.ordinal()];
            if (i == 1) {
                return 100;
            }
            if (i == 2) {
                return 90;
            }
            if (i == 3) {
                return 70;
            }
            if (i == 4) {
                return 60;
            }
            throw new NoWhenBranchMatchedException();
        }
    }

    /** 系统状态类型（jadx 未能恢复 enum，此处重建为真正的 Java enum） */
    public enum Type {
        FLASHLIGHT,
        CHARGING,
        SCREEN_RECORD,
        HOTSPOT
    }

    public IslandSystemStatus(Drawable icon, String title, String text, long j, boolean z, Type type, String sourceLabel, String connInfo) {
        Intrinsics.checkNotNullParameter(icon, "icon");
        Intrinsics.checkNotNullParameter(title, "title");
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(type, "type");
        Intrinsics.checkNotNullParameter(sourceLabel, "sourceLabel");
        Intrinsics.checkNotNullParameter(connInfo, "connInfo");
        this.icon = icon;
        this.title = title;
        this.text = text;
        this.durationMs = j;
        this.persistent = z;
        this.type = type;
        this.sourceLabel = sourceLabel;
        this.connInfo = connInfo;
    }

    public /* synthetic */ IslandSystemStatus(Drawable drawable, String str, String str2, long j, boolean z, Type type, String str3, String str4, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(drawable, str, (i & 4) != 0 ? "" : str2, (i & 8) != 0 ? 3500L : j, (i & 16) != 0 ? false : z, type, (i & 64) != 0 ? "系统状态" : str3, (i & 128) != 0 ? "" : str4);
    }

    /* renamed from: component1, reason: from getter */
    public final Drawable component1() {
        return this.icon;
    }

    /* renamed from: component2, reason: from getter */
    public final String component2() {
        return this.title;
    }

    /* renamed from: component3, reason: from getter */
    public final String component3() {
        return this.text;
    }

    /* renamed from: component4, reason: from getter */
    public final long component4() {
        return this.durationMs;
    }

    /* renamed from: component5, reason: from getter */
    public final boolean component5() {
        return this.persistent;
    }

    /* renamed from: component6, reason: from getter */
    public final Type component6() {
        return this.type;
    }

    /* renamed from: component7, reason: from getter */
    public final String component7() {
        return this.sourceLabel;
    }

    /* renamed from: component8, reason: from getter */
    public final String component8() {
        return this.connInfo;
    }

    public final IslandSystemStatus copy(Drawable icon, String title, String text, long durationMs, boolean persistent, Type type, String sourceLabel, String connInfo) {
        Intrinsics.checkNotNullParameter(icon, "icon");
        Intrinsics.checkNotNullParameter(title, "title");
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(type, "type");
        Intrinsics.checkNotNullParameter(sourceLabel, "sourceLabel");
        Intrinsics.checkNotNullParameter(connInfo, "connInfo");
        return new IslandSystemStatus(icon, title, text, durationMs, persistent, type, sourceLabel, connInfo);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof IslandSystemStatus)) {
            return false;
        }
        IslandSystemStatus islandSystemStatus = (IslandSystemStatus) other;
        return Intrinsics.areEqual(this.icon, islandSystemStatus.icon) && Intrinsics.areEqual(this.title, islandSystemStatus.title) && Intrinsics.areEqual(this.text, islandSystemStatus.text) && this.durationMs == islandSystemStatus.durationMs && this.persistent == islandSystemStatus.persistent && this.type == islandSystemStatus.type && Intrinsics.areEqual(this.sourceLabel, islandSystemStatus.sourceLabel) && Intrinsics.areEqual(this.connInfo, islandSystemStatus.connInfo);
    }

    public final String getConnInfo() {
        return this.connInfo;
    }

    public final long getDurationMs() {
        return this.durationMs;
    }

    public final Drawable getIcon() {
        return this.icon;
    }

    public final boolean getPersistent() {
        return this.persistent;
    }

    public final String getSourceLabel() {
        return this.sourceLabel;
    }

    public final String getText() {
        return this.text;
    }

    public final String getTitle() {
        return this.title;
    }

    public final Type getType() {
        return this.type;
    }

    public int hashCode() {
        return (((((((((((((this.icon.hashCode() * 31) + this.title.hashCode()) * 31) + this.text.hashCode()) * 31) + Long.hashCode(this.durationMs)) * 31) + Boolean.hashCode(this.persistent)) * 31) + this.type.hashCode()) * 31) + this.sourceLabel.hashCode()) * 31) + this.connInfo.hashCode());
    }

    public String toString() {
        return "IslandSystemStatus(icon=" + this.icon + ", title=" + this.text + ", durationMs=" + this.durationMs + ", persistent=" + this.persistent + ", type=" + this.type + ", sourceLabel=" + this.sourceLabel + ", connInfo=" + this.connInfo + ")";
    }
}
