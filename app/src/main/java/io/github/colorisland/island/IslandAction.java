package io.github.colorisland.island;

import android.app.PendingIntent;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: IslandNotification.kt */
/* loaded from: classes3.dex */
public final /* data */ class IslandAction {
    public static final int $stable = 8;
    private final PendingIntent intent;
    private final String title;

    public IslandAction(String title, PendingIntent intent) {
        Intrinsics.checkNotNullParameter(title, "title");
        Intrinsics.checkNotNullParameter(intent, "intent");
        this.title = title;
        this.intent = intent;
    }

    public static /* synthetic */ IslandAction copy$default(IslandAction islandAction, String str, PendingIntent pendingIntent, int i, Object obj) {
        if ((i & 1) != 0) {
            str = islandAction.title;
        }
        if ((i & 2) != 0) {
            pendingIntent = islandAction.intent;
        }
        return islandAction.copy(str, pendingIntent);
    }

    /* renamed from: component1, reason: from getter */
    public final String component1() {
        return this.title;
    }

    /* renamed from: component2, reason: from getter */
    public final PendingIntent component2() {
        return this.intent;
    }

    public final IslandAction copy(String title, PendingIntent intent) {
        Intrinsics.checkNotNullParameter(title, "title");
        Intrinsics.checkNotNullParameter(intent, "intent");
        return new IslandAction(title, intent);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof IslandAction)) {
            return false;
        }
        IslandAction islandAction = (IslandAction) other;
        return Intrinsics.areEqual(this.title, islandAction.title) && Intrinsics.areEqual(this.intent, islandAction.intent);
    }

    public final PendingIntent getIntent() {
        return this.intent;
    }

    public final String getTitle() {
        return this.title;
    }

    public int hashCode() {
        return (this.title.hashCode() * 31) + this.intent.hashCode();
    }

    public String toString() {
        return "IslandAction(title=" + this.title + ", intent=" + this.intent + ")";
    }
}
