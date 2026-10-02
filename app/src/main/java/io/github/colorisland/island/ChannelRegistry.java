package io.github.colorisland.island;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import io.github.colorisland.IslandConfig;
import io.github.colorisland.island.ChannelRegistry;
import io.github.libxposed.api.XposedModule;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import io.github.colorisland.util.KResult;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: ChannelRegistry.kt */
/* loaded from: classes3.dex */
public final class ChannelRegistry {
    private static final String CATALOG_KEY = "channel_catalog";
    private static final int MAX_CHANNELS_PER_PKG = 50;
    private static final int MAX_NAME_LEN = 40;
    private static final long PERSIST_INTERVAL_MS = 3000;
    private static volatile boolean dirty;
    private static volatile long lastPersistMs;
    private static volatile SharedPreferences prefs;
    public static final ChannelRegistry INSTANCE = new ChannelRegistry();
    private static final ConcurrentHashMap<String, Map<String, ChannelMeta>> cache = new ConcurrentHashMap<>();
    public static final int $stable = 8;

    /* compiled from: ChannelRegistry.kt */
    /* loaded from: classes3.dex */
    public static final /* data */ class ChannelMeta {
        public static final int $stable = 8;
        private final String channelId;
        private int count;
        private long lastSeen;
        private final String name;

        public ChannelMeta(String channelId, String name, int i, long j) {
            Intrinsics.checkNotNullParameter(channelId, "channelId");
            Intrinsics.checkNotNullParameter(name, "name");
            this.channelId = channelId;
            this.name = name;
            this.count = i;
            this.lastSeen = j;
        }

        public static /* synthetic */ ChannelMeta copy$default(ChannelMeta channelMeta, String str, String str2, int i, long j, int i2, Object obj) {
            if ((i2 & 1) != 0) {
                str = channelMeta.channelId;
            }
            if ((i2 & 2) != 0) {
                str2 = channelMeta.name;
            }
            String str3 = str2;
            if ((i2 & 4) != 0) {
                i = channelMeta.count;
            }
            int i3 = i;
            if ((i2 & 8) != 0) {
                j = channelMeta.lastSeen;
            }
            return channelMeta.copy(str, str3, i3, j);
        }

        /* renamed from: component1, reason: from getter */
        public final String component1() {
            return this.channelId;
        }

        /* renamed from: component2, reason: from getter */
        public final String component2() {
            return this.name;
        }

        /* renamed from: component3, reason: from getter */
        public final int component3() {
            return this.count;
        }

        /* renamed from: component4, reason: from getter */
        public final long component4() {
            return this.lastSeen;
        }

        public final ChannelMeta copy(String channelId, String name, int count, long lastSeen) {
            Intrinsics.checkNotNullParameter(channelId, "channelId");
            Intrinsics.checkNotNullParameter(name, "name");
            return new ChannelMeta(channelId, name, count, lastSeen);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ChannelMeta)) {
                return false;
            }
            ChannelMeta channelMeta = (ChannelMeta) other;
            return Intrinsics.areEqual(this.channelId, channelMeta.channelId) && Intrinsics.areEqual(this.name, channelMeta.name) && this.count == channelMeta.count && this.lastSeen == channelMeta.lastSeen;
        }

        public final String getChannelId() {
            return this.channelId;
        }

        public final int getCount() {
            return this.count;
        }

        public final long getLastSeen() {
            return this.lastSeen;
        }

        public final String getName() {
            return this.name;
        }

        public int hashCode() {
            return (((((this.channelId.hashCode() * 31) + this.name.hashCode()) * 31) + Integer.hashCode(this.count)) * 31) + Long.hashCode(this.lastSeen);
        }

        public final void setCount(int i) {
            this.count = i;
        }

        public final void setLastSeen(long j) {
            this.lastSeen = j;
        }

        public String toString() {
            return "ChannelMeta(channelId=" + this.channelId + ", name=" + this.name + ", count=" + this.count + ", lastSeen=" + this.lastSeen + ")";
        }
    }

    private ChannelRegistry() {
    }

    private final void loadFromPrefs() {
        Object m5221constructorimpl;
        Object m5221constructorimpl2;
        JSONObject jSONObject;
        int i;
        JSONObject jSONObject2;
        SharedPreferences sharedPreferences = prefs;
        if (sharedPreferences == null) {
            return;
        }
        try {
            ChannelRegistry channelRegistry = this;
            m5221constructorimpl = KResult.success(sharedPreferences.getString("channel_catalog", ""));
        } catch (Throwable th) {
            m5221constructorimpl = KResult.success(KResult.createFailure(th));
        }
        if (KResult.isFailureimpl(m5221constructorimpl)) {
            m5221constructorimpl = null;
        }
        String str = (String) m5221constructorimpl;
        String str2 = str != null ? str : "";
        if (StringsKt.isBlank(str2)) {
            return;
        }
        try {
            ChannelRegistry channelRegistry2 = this;
            JSONObject jSONObject3 = new JSONObject(str2);
            Iterator<String> keys = jSONObject3.keys();
            Intrinsics.checkNotNullExpressionValue(keys, "keys(...)");
            while (keys.hasNext()) {
                String next = keys.next();
                JSONArray optJSONArray = jSONObject3.optJSONArray(next);
                if (optJSONArray == null) {
                    jSONObject = jSONObject3;
                } else {
                    ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
                    int length = optJSONArray.length();
                    int i2 = 0;
                    int i3 = 0;
                    while (i3 < length) {
                        JSONObject optJSONObject = optJSONArray.optJSONObject(i3);
                        if (optJSONObject != null) {
                            String optString = optJSONObject.optString("id");
                            Intrinsics.checkNotNull(optString);
                            if (!StringsKt.isBlank(optString)) {
                                String optString2 = optJSONObject.optString("name", optString);
                                Intrinsics.checkNotNullExpressionValue(optString2, "optString(...)");
                                i = i3;
                                jSONObject2 = jSONObject3;
                                concurrentHashMap.put(optString, new ChannelMeta(optString, optString2, optJSONObject.optInt("count", i2), optJSONObject.optLong("lastSeen", 0L)));
                                i3 = i + 1;
                                jSONObject3 = jSONObject2;
                                i2 = 0;
                            }
                        }
                        jSONObject2 = jSONObject3;
                        i = i3;
                        i3 = i + 1;
                        jSONObject3 = jSONObject2;
                        i2 = 0;
                    }
                    jSONObject = jSONObject3;
                    if (!concurrentHashMap.isEmpty()) {
                        cache.put(next, concurrentHashMap);
                    }
                }
                jSONObject3 = jSONObject;
            }
            m5221constructorimpl2 = KResult.success(Unit.INSTANCE);
        } catch (Throwable th2) {
            m5221constructorimpl2 = KResult.success(KResult.createFailure(th2));
        }
        Throwable m5224exceptionOrNullimpl = KResult.exceptionOrNullimpl(m5221constructorimpl2);
        if (m5224exceptionOrNullimpl != null) {
            Log.w("ChannelRegistry", "loadFromPrefs failed: " + m5224exceptionOrNullimpl.getMessage());
        }
    }

    private final String resolveChannelName(Context context, String pkg, String channelId) {
        Object m5221constructorimpl;
        NotificationManager notificationManager = null;
        String str;
        CharSequence name;
        try {
            ChannelRegistry channelRegistry = this;
            notificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
        } catch (Throwable th) {
            m5221constructorimpl = KResult.success(KResult.createFailure(th));
        }
        if (notificationManager == null) {
            return null;
        }
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(pkg, channelId);
        if (notificationChannel == null || (name = notificationChannel.getName()) == null || (str = name.toString()) == null || !(!StringsKt.isBlank(str))) {
            str = null;
        }
        m5221constructorimpl = KResult.success(str);
        return (String) (KResult.isFailureimpl(m5221constructorimpl) ? null : m5221constructorimpl);
    }

    private final String serialize() {
        try {
            JSONObject jSONObject = new JSONObject();
        for (Map.Entry<String, Map<String, ChannelMeta>> entry : cache.entrySet()) {
            String key = entry.getKey();
            Map<String, ChannelMeta> value = entry.getValue();
            JSONArray jSONArray = new JSONArray();
            for (ChannelMeta channelMeta : CollectionsKt.sortedWith(value.values(), new Comparator<ChannelMeta>() { // from class: io.github.colorisland.island.ChannelRegistry$serialize$$inlined$sortedByDescending$1
                @Override // java.util.Comparator
                public final int compare(ChannelMeta t, ChannelMeta t2) {
                    return ComparisonsKt.compareValues(Integer.valueOf(t2.getCount()), Integer.valueOf(t.getCount()));
                }
            })) {
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("id", channelMeta.getChannelId());
                jSONObject2.put("name", channelMeta.getName());
                jSONObject2.put("count", channelMeta.getCount());
                jSONObject2.put("lastSeen", channelMeta.getLastSeen());
                jSONArray.put(jSONObject2);
            }
            jSONObject.put(key, jSONArray);
        }
            String jSONObject3 = jSONObject.toString();
            Intrinsics.checkNotNullExpressionValue(jSONObject3, "toString(...)");
            return jSONObject3;
        } catch (JSONException e) {
            return null;
        }
    }

    public final List<ChannelMeta> getChannels(String pkg) {
        Collection<ChannelMeta> values;
        List<ChannelMeta> sortedWith;
        Intrinsics.checkNotNullParameter(pkg, "pkg");
        Map<String, ChannelMeta> map = cache.get(pkg);
        return (map == null || (values = map.values()) == null || (sortedWith = CollectionsKt.sortedWith(values, new Comparator<ChannelRegistry.ChannelMeta>() { // from class: io.github.colorisland.island.ChannelRegistry$getChannels$$inlined$sortedByDescending$1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.util.Comparator
            public final int compare(ChannelRegistry.ChannelMeta t, ChannelRegistry.ChannelMeta t2) {
                return ComparisonsKt.compareValues(Integer.valueOf(t2.getCount()), Integer.valueOf(t.getCount()));
            }
        })) == null) ? CollectionsKt.emptyList() : sortedWith;
    }

    public final void init(XposedModule module) {
        Object m5221constructorimpl;
        Intrinsics.checkNotNullParameter(module, "module");
        if (prefs != null) {
            return;
        }
        try {
            ChannelRegistry channelRegistry = this;
            m5221constructorimpl = KResult.success(module.getRemotePreferences(IslandConfig.PREFS_NAME));
        } catch (Throwable th) {
            m5221constructorimpl = KResult.success(KResult.createFailure(th));
        }
        if (KResult.isFailureimpl(m5221constructorimpl)) {
            m5221constructorimpl = null;
        }
        prefs = (SharedPreferences) m5221constructorimpl;
        loadFromPrefs();
    }

    public final void observe(Context context, StatusBarNotification sbn) {
        Object m5221constructorimpl;
        Map<String, ChannelMeta> putIfAbsent;
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(sbn, "sbn");
        Notification notification = sbn.getNotification();
        if (notification == null) {
            return;
        }
        String packageName = sbn.getPackageName();
        try {
            ChannelRegistry channelRegistry = this;
            m5221constructorimpl = KResult.success(notification.getChannelId());
        } catch (Throwable th) {
            m5221constructorimpl = KResult.success(KResult.createFailure(th));
        }
        if (KResult.isFailureimpl(m5221constructorimpl)) {
            m5221constructorimpl = null;
        }
        String str = (String) m5221constructorimpl;
        if (str != null) {
            String str2 = StringsKt.isBlank(str) ^ true ? str : null;
            if (str2 == null) {
                return;
            }
            ConcurrentHashMap<String, Map<String, ChannelMeta>> concurrentHashMap = cache;
            Map<String, ChannelMeta> concurrentHashMap2 = concurrentHashMap.get(packageName);
            if (concurrentHashMap2 == null && (putIfAbsent = concurrentHashMap.putIfAbsent(packageName, (concurrentHashMap2 = new ConcurrentHashMap<String, ChannelMeta>()))) != null) {
                concurrentHashMap2 = putIfAbsent;
            }
            Map<String, ChannelMeta> map = concurrentHashMap2;
            Intrinsics.checkNotNull(map);
            synchronized (map) {
                ChannelMeta channelMeta = map.get(str2);
                if (channelMeta != null) {
                    channelMeta.setCount(channelMeta.getCount() + 1);
                    channelMeta.setLastSeen(System.currentTimeMillis());
                } else if (map.size() < 50) {
                    ChannelRegistry channelRegistry2 = INSTANCE;
                    Intrinsics.checkNotNull(packageName);
                    String resolveChannelName = channelRegistry2.resolveChannelName(context, packageName, str2);
                    if (resolveChannelName == null) {
                        resolveChannelName = str2;
                    }
                    if (resolveChannelName.length() > 40) {
                        resolveChannelName = StringsKt.take(resolveChannelName, 40);
                    }
                    map.put(str2, new ChannelMeta(str2, resolveChannelName, 1, System.currentTimeMillis()));
                    dirty = true;
                }
                Unit unit = Unit.INSTANCE;
            }
        }
    }

    public final void persistIfNeeded() {
        Object m5221constructorimpl;
        Object m5221constructorimpl2;
        if (dirty) {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - lastPersistMs < PERSIST_INTERVAL_MS) {
                return;
            }
            lastPersistMs = currentTimeMillis;
            SharedPreferences sharedPreferences = prefs;
            if (sharedPreferences == null) {
                return;
            }
            try {
                ChannelRegistry channelRegistry = this;
                m5221constructorimpl = KResult.success(serialize());
            } catch (Throwable th) {
                m5221constructorimpl = KResult.success(KResult.createFailure(th));
            }
            if (KResult.isFailureimpl(m5221constructorimpl)) {
                m5221constructorimpl = null;
            }
            String str = (String) m5221constructorimpl;
            if (str == null) {
                return;
            }
            dirty = false;
            try {
                ChannelRegistry channelRegistry2 = this;
                sharedPreferences.edit().putString("channel_catalog", str).apply();
                m5221constructorimpl2 = KResult.success(Unit.INSTANCE);
            } catch (Throwable th2) {
                m5221constructorimpl2 = KResult.success(KResult.createFailure(th2));
            }
            Throwable m5224exceptionOrNullimpl = KResult.exceptionOrNullimpl(m5221constructorimpl2);
            if (m5224exceptionOrNullimpl != null) {
                dirty = true;
                Log.w("ChannelRegistry", "persist failed: " + m5224exceptionOrNullimpl.getMessage());
            }
        }
    }
}
