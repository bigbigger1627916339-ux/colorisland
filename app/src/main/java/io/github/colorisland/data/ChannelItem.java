package io.github.colorisland.data;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ChannelItem.kt */
/* loaded from: classes3.dex */
public final /* data */ class ChannelItem {
    public static final int $stable = 0;
    private final int count;
    private final String id;
    private final String name;

    public ChannelItem(String id, String name, int i) {
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(name, "name");
        this.id = id;
        this.name = name;
        this.count = i;
    }

    public static /* synthetic */ ChannelItem copy$default(ChannelItem channelItem, String str, String str2, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            str = channelItem.id;
        }
        if ((i2 & 2) != 0) {
            str2 = channelItem.name;
        }
        if ((i2 & 4) != 0) {
            i = channelItem.count;
        }
        return channelItem.copy(str, str2, i);
    }

    /* renamed from: component1, reason: from getter */
    public final String component1() {
        return this.id;
    }

    /* renamed from: component2, reason: from getter */
    public final String component2() {
        return this.name;
    }

    /* renamed from: component3, reason: from getter */
    public final int component3() {
        return this.count;
    }

    public final ChannelItem copy(String id, String name, int count) {
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(name, "name");
        return new ChannelItem(id, name, count);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ChannelItem)) {
            return false;
        }
        ChannelItem channelItem = (ChannelItem) other;
        return Intrinsics.areEqual(this.id, channelItem.id) && Intrinsics.areEqual(this.name, channelItem.name) && this.count == channelItem.count;
    }

    public final int getCount() {
        return this.count;
    }

    public final String getId() {
        return this.id;
    }

    public final String getName() {
        return this.name;
    }

    public int hashCode() {
        return (((this.id.hashCode() * 31) + this.name.hashCode()) * 31) + Integer.hashCode(this.count);
    }

    public String toString() {
        return "ChannelItem(id=" + this.id + ", name=" + this.name + ", count=" + this.count + ")";
    }
}
