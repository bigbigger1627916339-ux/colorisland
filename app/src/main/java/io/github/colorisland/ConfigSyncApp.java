package io.github.colorisland;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import io.github.colorisland.data.ChannelItem;
import io.github.colorisland.data.PrefsRepository;
import io.github.libxposed.service.XposedService;
import io.github.libxposed.service.XposedServiceHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import io.github.colorisland.util.KResult;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: ConfigSyncApp.kt */
/* loaded from: classes3.dex */
public final class ConfigSyncApp extends Application implements XposedServiceHelper.OnServiceListener {
    public static final String PREFS_FILE_NAME = "colorisland_settings";
    public static final String TAG = "ColorIsland";
    private final SharedPreferences.OnSharedPreferenceChangeListener changeListener = new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: io.github.colorisland.ConfigSyncApp$$ExternalSyntheticLambda0
        @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
        public final void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
            ConfigSyncApp.this.syncKey(str);
        }
    };
    private PrefsRepository prefsRepo;
    private XposedService service;
    private volatile boolean xposedServiceBound;
    public static final int $stable = 8;

    private final Map<String, List<ChannelItem>> parseCatalog(String raw) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        try {
            JSONObject jSONObject = new JSONObject(raw);
        Iterator<String> keys = jSONObject.keys();
        Intrinsics.checkNotNullExpressionValue(keys, "keys(...)");
        while (keys.hasNext()) {
            String next = keys.next();
            JSONArray optJSONArray = jSONObject.optJSONArray(next);
            if (optJSONArray != null) {
                ArrayList arrayList = new ArrayList(optJSONArray.length());
                int length = optJSONArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                    if (optJSONObject != null) {
                        String optString = optJSONObject.optString("id");
                        Intrinsics.checkNotNull(optString);
                        if (!StringsKt.isBlank(optString)) {
                            String optString2 = optJSONObject.optString("name", optString);
                            Intrinsics.checkNotNullExpressionValue(optString2, "optString(...)");
                            arrayList.add(new ChannelItem(optString, optString2, optJSONObject.optInt("count", 0)));
                        }
                    }
                }
                if (!arrayList.isEmpty()) {
                    linkedHashMap.put(next, arrayList);
                }
                }
            }
            return linkedHashMap;
        } catch (JSONException e) {
            return linkedHashMap;
        }
    }

    private final void syncAll() {
        Object m5221constructorimpl;
        XposedService xposedService = this.service;
        if (xposedService == null) {
            return;
        }
        Map<String, ?> all = getPrefsRepo().getPrefs().getAll();
        try {
            ConfigSyncApp configSyncApp = this;
            SharedPreferences remotePreferences = xposedService.getRemotePreferences(IslandConfig.PREFS_NAME);
            Intrinsics.checkNotNullExpressionValue(remotePreferences, "getRemotePreferences(...)");
            SharedPreferences.Editor edit = remotePreferences.edit();
            Intrinsics.checkNotNull(all);
            for (Map.Entry<String, ?> entry : all.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof Boolean) {
                    edit.putBoolean(key, ((Boolean) value).booleanValue());
                } else if (value instanceof Integer) {
                    edit.putInt(key, ((Number) value).intValue());
                } else if (value instanceof Long) {
                    edit.putLong(key, ((Number) value).longValue());
                } else if (value instanceof String) {
                    edit.putString(key, (String) value);
                }
            }
            m5221constructorimpl = KResult.success(Boolean.valueOf(edit.commit()));
        } catch (Throwable th) {
            m5221constructorimpl = KResult.success(KResult.createFailure(th));
        }
        Throwable m5224exceptionOrNullimpl = KResult.exceptionOrNullimpl(m5221constructorimpl);
        if (m5224exceptionOrNullimpl != null) {
            Log.w("ColorIsland", "syncAll failed: " + m5224exceptionOrNullimpl.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void syncKey(String key) {
        Object m5221constructorimpl;
        XposedService xposedService = this.service;
        if (xposedService == null || key == null) {
            return;
        }
        try {
            ConfigSyncApp configSyncApp = this;
            SharedPreferences remotePreferences = xposedService.getRemotePreferences(IslandConfig.PREFS_NAME);
            Intrinsics.checkNotNullExpressionValue(remotePreferences, "getRemotePreferences(...)");
            SharedPreferences.Editor edit = remotePreferences.edit();
            Object obj = getPrefsRepo().getPrefs().getAll().get(key);
            if (obj instanceof Boolean) {
                edit.putBoolean(key, ((Boolean) obj).booleanValue());
            } else if (obj instanceof Integer) {
                edit.putInt(key, ((Number) obj).intValue());
            } else if (obj instanceof Long) {
                edit.putLong(key, ((Number) obj).longValue());
            } else if (obj instanceof String) {
                edit.putString(key, (String) value);
            } else {
                edit.remove(key);
            }
            m5221constructorimpl = KResult.success(Boolean.valueOf(edit.commit()));
        } catch (Throwable th) {
            m5221constructorimpl = KResult.success(KResult.createFailure(th));
        }
        Throwable m5224exceptionOrNullimpl = KResult.exceptionOrNullimpl(m5221constructorimpl);
        if (m5224exceptionOrNullimpl != null) {
            Log.w("ColorIsland", "syncKey(" + key + ") failed: " + m5224exceptionOrNullimpl.getMessage());
        }
    }

    public final PrefsRepository getPrefsRepo() {
        PrefsRepository prefsRepository = this.prefsRepo;
        if (prefsRepository != null) {
            return prefsRepository;
        }
        Intrinsics.throwUninitializedPropertyAccessException("prefsRepo");
        return null;
    }

    public final boolean getXposedServiceBound() {
        return this.xposedServiceBound;
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_FILE_NAME, 0);
        Intrinsics.checkNotNullExpressionValue(sharedPreferences, "getSharedPreferences(...)");
        this.prefsRepo = new PrefsRepository(sharedPreferences);
        getPrefsRepo().getPrefs().registerOnSharedPreferenceChangeListener(getPrefsRepo());
        getPrefsRepo().getPrefs().registerOnSharedPreferenceChangeListener(this.changeListener);
        XposedServiceHelper.registerListener(this);
    }

    @Override // io.github.libxposed.service.XposedServiceHelper.OnServiceListener
    public void onServiceBind(XposedService service) {
        Intrinsics.checkNotNullParameter(service, "service");
        this.service = service;
        this.xposedServiceBound = true;
        syncAll();
    }

    @Override // io.github.libxposed.service.XposedServiceHelper.OnServiceListener
    public void onServiceDied(XposedService service) {
        Intrinsics.checkNotNullParameter(service, "service");
        if (this.service == service) {
            this.service = null;
            this.xposedServiceBound = false;
        }
    }

    @Override // android.app.Application
    public void onTerminate() {
        getPrefsRepo().getPrefs().unregisterOnSharedPreferenceChangeListener(this.changeListener);
        getPrefsRepo().getPrefs().unregisterOnSharedPreferenceChangeListener(getPrefsRepo());
        super.onTerminate();
    }

    public final Map<String, List<ChannelItem>> readChannelCatalog() {
        Object m5221constructorimpl;
        String str = "";
        XposedService xposedService = this.service;
        if (xposedService == null) {
            return MapsKt.emptyMap();
        }
        try {
            ConfigSyncApp configSyncApp = this;
            SharedPreferences remotePreferences = xposedService.getRemotePreferences(IslandConfig.PREFS_NAME);
            Intrinsics.checkNotNullExpressionValue(remotePreferences, "getRemotePreferences(...)");
            String string = remotePreferences.getString(IslandConfig.CATALOG_KEY_CHANNELS, "");
            if (string != null) {
                str = string;
            }
            m5221constructorimpl = KResult.success(StringsKt.isBlank(str) ? MapsKt.emptyMap() : parseCatalog(str));
        } catch (Throwable th) {
            m5221constructorimpl = KResult.success(KResult.createFailure(th));
        }
        Throwable m5224exceptionOrNullimpl = KResult.exceptionOrNullimpl(m5221constructorimpl);
        if (m5224exceptionOrNullimpl != null) {
            Log.w("ColorIsland", "readChannelCatalog failed: " + m5224exceptionOrNullimpl.getMessage());
            m5221constructorimpl = MapsKt.emptyMap();
        }
        return (Map) m5221constructorimpl;
    }
}
