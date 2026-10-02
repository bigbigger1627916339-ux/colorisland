package io.github.colorisland.data;

import android.graphics.drawable.Drawable;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: AppsRepository.kt */
/* loaded from: classes3.dex */
public final /* data */ class AppEntry {
    public static final int $stable = 8;
    private final Drawable icon;
    private final boolean isSystem;
    private final String label;
    private final String pkg;

    public AppEntry(String pkg, String label, Drawable drawable, boolean z) {
        Intrinsics.checkNotNullParameter(pkg, "pkg");
        Intrinsics.checkNotNullParameter(label, "label");
        this.pkg = pkg;
        this.label = label;
        this.icon = drawable;
        this.isSystem = z;
    }

    public static /* synthetic */ AppEntry copy$default(AppEntry appEntry, String str, String str2, Drawable drawable, boolean z, int i, Object obj) {
        if ((i & 1) != 0) {
            str = appEntry.pkg;
        }
        if ((i & 2) != 0) {
            str2 = appEntry.label;
        }
        if ((i & 4) != 0) {
            drawable = appEntry.icon;
        }
        if ((i & 8) != 0) {
            z = appEntry.isSystem;
        }
        return appEntry.copy(str, str2, drawable, z);
    }

    /* renamed from: component1, reason: from getter */
    public final String component1() {
        return this.pkg;
    }

    /* renamed from: component2, reason: from getter */
    public final String component2() {
        return this.label;
    }

    /* renamed from: component3, reason: from getter */
    public final Drawable component3() {
        return this.icon;
    }

    /* renamed from: component4, reason: from getter */
    public final boolean component4() {
        return this.isSystem;
    }

    public final AppEntry copy(String pkg, String label, Drawable icon, boolean isSystem) {
        Intrinsics.checkNotNullParameter(pkg, "pkg");
        Intrinsics.checkNotNullParameter(label, "label");
        return new AppEntry(pkg, label, icon, isSystem);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AppEntry)) {
            return false;
        }
        AppEntry appEntry = (AppEntry) other;
        return Intrinsics.areEqual(this.pkg, appEntry.pkg) && Intrinsics.areEqual(this.label, appEntry.label) && Intrinsics.areEqual(this.icon, appEntry.icon) && this.isSystem == appEntry.isSystem;
    }

    public final Drawable getIcon() {
        return this.icon;
    }

    public final String getLabel() {
        return this.label;
    }

    public final String getPkg() {
        return this.pkg;
    }

    public int hashCode() {
        int hashCode = ((this.pkg.hashCode() * 31) + this.label.hashCode()) * 31;
        Drawable drawable = this.icon;
        return ((hashCode + (drawable == null ? 0 : drawable.hashCode())) * 31) + Boolean.hashCode(this.isSystem);
    }

    public final boolean isSystem() {
        return this.isSystem;
    }

    public String toString() {
        return "AppEntry(pkg=" + this.pkg + ", label=" + this.label + ", icon=" + this.icon + ", isSystem=" + this.isSystem + ")";
    }
}
