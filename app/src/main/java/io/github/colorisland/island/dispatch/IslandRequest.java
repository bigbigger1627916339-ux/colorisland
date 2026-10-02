package io.github.colorisland.island.dispatch;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Parcelable;
import io.github.colorisland.island.IslandAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import io.github.colorisland.util.KResult;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;

/* compiled from: IslandRequest.kt */
/* loaded from: classes3.dex */
public final /* data */ class IslandRequest {
    private final List<IslandAction> actions;
    private final String appLabel;
    private final PendingIntent contentIntent;
    private final Icon icon;
    private final String id;
    private final boolean persistent;
    private final String pkg;
    private final String subText;
    private final String text;
    private final long timeoutMs;
    private final String title;

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    public static final int $stable = 8;

    /* compiled from: IslandRequest.kt */
    /* loaded from: classes3.dex */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private final <T extends Parcelable> T readParcelable(Intent intent, String key) {
            Object m5221constructorimpl;
            try {
                Companion companion2 = this;
                m5221constructorimpl = KResult.success(intent.getParcelableExtra(key));
            } catch (Throwable th) {
                m5221constructorimpl = KResult.success(KResult.createFailure(th));
            }
            if (KResult.isFailureimpl(m5221constructorimpl)) {
                m5221constructorimpl = null;
            }
            return (T) m5221constructorimpl;
        }

        private final <T extends Parcelable> ArrayList<T> readParcelableList(Intent intent, String key) {
            Object m5221constructorimpl;
            try {
                Companion companion2 = this;
                m5221constructorimpl = KResult.success(intent.getParcelableArrayListExtra(key));
            } catch (Throwable th) {
                m5221constructorimpl = KResult.success(KResult.createFailure(th));
            }
            if (KResult.isFailureimpl(m5221constructorimpl)) {
                m5221constructorimpl = null;
            }
            return (ArrayList) m5221constructorimpl;
        }

        public final IslandRequest fromIntent(Intent intent) {
            boolean z;
            List emptyList;
            Intrinsics.checkNotNullParameter(intent, "intent");
            String stringExtra = intent.getStringExtra(IslandDispatchContract.EXTRA_ID);
            if (stringExtra != null) {
                if (!(!StringsKt.isBlank(stringExtra))) {
                    stringExtra = null;
                }
                String str = stringExtra;
                if (str != null) {
                    String stringExtra2 = intent.getStringExtra(IslandDispatchContract.EXTRA_TITLE);
                    if (stringExtra2 == null) {
                        stringExtra2 = "";
                    }
                    String stringExtra3 = intent.getStringExtra(IslandDispatchContract.EXTRA_TEXT);
                    if (stringExtra3 == null) {
                        stringExtra3 = "";
                    }
                    if (StringsKt.isBlank(stringExtra2) && StringsKt.isBlank(stringExtra3)) {
                        throw new IllegalArgumentException("title and text both blank");
                    }
                    String take = StringsKt.take(stringExtra2, 50);
                    String take2 = StringsKt.take(stringExtra3, 200);
                    String stringExtra4 = intent.getStringExtra(IslandDispatchContract.EXTRA_SUB_TEXT);
                    if (stringExtra4 == null) {
                        stringExtra4 = "";
                    }
                    String take3 = StringsKt.take(stringExtra4, 50);
                    String stringExtra5 = intent.getStringExtra(IslandDispatchContract.EXTRA_PKG);
                    String str2 = stringExtra5 == null ? "" : stringExtra5;
                    String stringExtra6 = intent.getStringExtra(IslandDispatchContract.EXTRA_APP_LABEL);
                    String take4 = StringsKt.take(stringExtra6 != null ? stringExtra6 : "", 50);
                    Icon icon = (Icon) readParcelable(intent, IslandDispatchContract.EXTRA_ICON);
                    PendingIntent pendingIntent = (PendingIntent) readParcelable(intent, IslandDispatchContract.EXTRA_CONTENT_INTENT);
                    long coerceIn = RangesKt.coerceIn(intent.getLongExtra(IslandDispatchContract.EXTRA_TIMEOUT_MS, IslandDispatchContract.DEFAULT_TIMEOUT_MS), 1L, IslandDispatchContract.MAX_TIMEOUT_MS);
                    boolean booleanExtra = intent.getBooleanExtra(IslandDispatchContract.EXTRA_PERSISTENT, false);
                    ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra(IslandDispatchContract.EXTRA_ACTION_TITLES);
                    ArrayList readParcelableList = readParcelableList(intent, IslandDispatchContract.EXTRA_ACTION_INTENTS);
                    if (stringArrayListExtra == null || readParcelableList == null) {
                        z = booleanExtra;
                        emptyList = CollectionsKt.emptyList();
                    } else {
                        ArrayList<String> arrayList = stringArrayListExtra;
                        Iterator it = arrayList.iterator();
                        ArrayList arrayList2 = readParcelableList;
                        Iterator it2 = arrayList2.iterator();
                        z = booleanExtra;
                        ArrayList arrayList3 = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(arrayList, 10), CollectionsKt.collectionSizeOrDefault(arrayList2, 10)));
                        while (it.hasNext() && it2.hasNext()) {
                            Object next = it.next();
                            PendingIntent pendingIntent2 = (PendingIntent) it2.next();
                            String str3 = (String) next;
                            Intrinsics.checkNotNull(str3);
                            arrayList3.add(new IslandAction(str3, pendingIntent2));
                        }
                        ArrayList arrayList4 = new ArrayList();
                        for (Object obj : arrayList3) {
                            if (!StringsKt.isBlank(((IslandAction) obj).getTitle())) {
                                arrayList4.add(obj);
                            }
                        }
                        emptyList = CollectionsKt.take(arrayList4, 3);
                    }
                    return new IslandRequest(str, take, take2, take3, str2, take4, icon, pendingIntent, coerceIn, z, emptyList);
                }
            }
            throw new IllegalArgumentException("missing extra: ID");
        }
    }

    public IslandRequest(String id, String title, String text, String subText, String pkg, String appLabel, Icon icon, PendingIntent pendingIntent, long j, boolean z, List<IslandAction> actions) {
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(title, "title");
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(subText, "subText");
        Intrinsics.checkNotNullParameter(pkg, "pkg");
        Intrinsics.checkNotNullParameter(appLabel, "appLabel");
        Intrinsics.checkNotNullParameter(actions, "actions");
        this.id = id;
        this.title = title;
        this.text = text;
        this.subText = subText;
        this.pkg = pkg;
        this.appLabel = appLabel;
        this.icon = icon;
        this.contentIntent = pendingIntent;
        this.timeoutMs = j;
        this.persistent = z;
        this.actions = actions;
    }

    public /* synthetic */ IslandRequest(String str, String str2, String str3, String str4, String str5, String str6, Icon icon, PendingIntent pendingIntent, long j, boolean z, List list, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, str2, str3, (i & 8) != 0 ? "" : str4, (i & 16) != 0 ? "" : str5, (i & 32) != 0 ? "" : str6, (i & 64) != 0 ? null : icon, (i & 128) != 0 ? null : pendingIntent, (i & 256) != 0 ? 5000L : j, (i & 512) != 0 ? false : z, (i & 1024) != 0 ? CollectionsKt.emptyList() : list);
    }

    /* renamed from: component1, reason: from getter */
    public final String component1() {
        return this.id;
    }

    /* renamed from: component10, reason: from getter */
    public final boolean component10() {
        return this.persistent;
    }

    public final List<IslandAction> component11() {
        return this.actions;
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
    public final String component4() {
        return this.subText;
    }

    /* renamed from: component5, reason: from getter */
    public final String component5() {
        return this.pkg;
    }

    /* renamed from: component6, reason: from getter */
    public final String component6() {
        return this.appLabel;
    }

    /* renamed from: component7, reason: from getter */
    public final Icon component7() {
        return this.icon;
    }

    /* renamed from: component8, reason: from getter */
    public final PendingIntent component8() {
        return this.contentIntent;
    }

    /* renamed from: component9, reason: from getter */
    public final long component9() {
        return this.timeoutMs;
    }

    public final IslandRequest copy(String id, String title, String text, String subText, String pkg, String appLabel, Icon icon, PendingIntent contentIntent, long timeoutMs, boolean persistent, List<IslandAction> actions) {
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(title, "title");
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(subText, "subText");
        Intrinsics.checkNotNullParameter(pkg, "pkg");
        Intrinsics.checkNotNullParameter(appLabel, "appLabel");
        Intrinsics.checkNotNullParameter(actions, "actions");
        return new IslandRequest(id, title, text, subText, pkg, appLabel, icon, contentIntent, timeoutMs, persistent, actions);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return false;
        }
        if (!(other instanceof IslandRequest)) {
            return false;
        }
        IslandRequest islandRequest = (IslandRequest) other;
        return Intrinsics.areEqual(this.id, islandRequest.id) && Intrinsics.areEqual(this.title, islandRequest.title) && Intrinsics.areEqual(this.text, islandRequest.text) && Intrinsics.areEqual(this.subText, islandRequest.subText) && Intrinsics.areEqual(this.pkg, islandRequest.pkg) && Intrinsics.areEqual(this.appLabel, islandRequest.appLabel) && Intrinsics.areEqual(this.icon, islandRequest.icon) && Intrinsics.areEqual(this.contentIntent, islandRequest.contentIntent) && this.timeoutMs == islandRequest.timeoutMs && this.persistent == islandRequest.persistent && Intrinsics.areEqual(this.actions, islandRequest.actions);
    }

    public final List<IslandAction> getActions() {
        return this.actions;
    }

    public final String getAppLabel() {
        return this.appLabel;
    }

    public final PendingIntent getContentIntent() {
        return this.contentIntent;
    }

    public final Icon getIcon() {
        return this.icon;
    }

    public final String getId() {
        return this.id;
    }

    public final boolean getPersistent() {
        return this.persistent;
    }

    public final String getPkg() {
        return this.pkg;
    }

    public final String getSubText() {
        return this.subText;
    }

    public final String getText() {
        return this.text;
    }

    public final long getTimeoutMs() {
        return this.timeoutMs;
    }

    public final String getTitle() {
        return this.title;
    }

    public int hashCode() {
        int hashCode = ((((((((((this.id.hashCode() * 31) + this.title.hashCode()) * 31) + this.text.hashCode()) * 31) + this.subText.hashCode()) * 31) + this.pkg.hashCode()) * 31) + this.appLabel.hashCode()) * 31;
        Icon icon = this.icon;
        int hashCode2 = (hashCode + (icon == null ? 0 : icon.hashCode())) * 31;
        PendingIntent pendingIntent = this.contentIntent;
        return ((((((hashCode2 + (pendingIntent != null ? pendingIntent.hashCode() : 0 : 0)) * 31) + Long.hashCode(this.timeoutMs)) * 31) + Boolean.hashCode(this.persistent)) * 31) + this.actions.hashCode();
    }

    public String toString() {
        return "IslandRequest(id=" + this.id + ", title=" + this.title + ", text=" + this.text + ", subText=" + this.subText + ", pkg=" + this.pkg + ", appLabel=" + this.appLabel + ", icon=" + this.icon + ", contentIntent=" + this.contentIntent + ", timeoutMs=" + this.timeoutMs + ", persistent=" + this.persistent + ", actions=" + this.actions + ")";
    }
}
