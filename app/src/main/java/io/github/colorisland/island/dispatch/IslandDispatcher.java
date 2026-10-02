package io.github.colorisland.island.dispatch;

import android.content.Context;
import android.content.Intent;
import io.github.colorisland.island.IslandController;
import io.github.colorisland.island.IslandNotification;
import io.github.libxposed.api.XposedModule;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import io.github.colorisland.util.KResult;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* compiled from: IslandDispatcher.kt */
/* loaded from: classes3.dex */
public final class IslandDispatcher {
    private static final int MAX_TRACKED_UIDS = 128;
    private static final long MIN_INTERVAL_MS = 1000;
    private static volatile Context appContext;
    private static volatile XposedModule module;
    private static volatile boolean registered;
    public static final IslandDispatcher INSTANCE = new IslandDispatcher();
    private static final ConcurrentHashMap<Integer, Long> lastPostByUid = new ConcurrentHashMap<>();
    public static final int $stable = 8;

    private IslandDispatcher() {
    }

    private final void log(int priority, String message) {
        Unit unit;
        try {
            IslandDispatcher islandDispatcher = this;
            XposedModule xposedModule = module;
            if (xposedModule != null) {
                xposedModule.log(priority, IslandDispatchContract.TAG, message);
                unit = Unit.INSTANCE;
            } else {
                unit = null;
            }
        } catch (Throwable th) {
        }
    }

    public final void logRejected(int uid) {
        log(5, "rejected sender uid=" + uid + " (missing io.github.colorisland.permission.SEND_ISLAND)");
    }

    public final void onCancel(Context context, Intent intent, int uid) {
        Object m5221constructorimpl = null;
        String stringExtra = null;
        String str = null;
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(intent, "intent");
        try {
            IslandDispatcher islandDispatcher = this;
            stringExtra = intent.getStringExtra(IslandDispatchContract.EXTRA_ID);
            str = stringExtra;
        } catch (Throwable th) {
            m5221constructorimpl = KResult.success(KResult.createFailure(th));
        }
        if (str != null && !StringsKt.isBlank(str)) {
            IslandController.INSTANCE.cancelDispatched(stringExtra);
            log(3, "cancel id=" + stringExtra + " uid=" + uid);
            m5221constructorimpl = KResult.success(Unit.INSTANCE);
            Throwable m5224exceptionOrNullimpl = KResult.exceptionOrNullimpl(m5221constructorimpl);
            if (m5224exceptionOrNullimpl != null) {
                INSTANCE.log(5, "onCancel failed: " + m5224exceptionOrNullimpl.getMessage());
            }
        }
    }

    public final void onShow(Context context, Intent intent, int uid) {
        Object m5221constructorimpl = null;
        IslandRequest fromIntent = null;
        long currentTimeMillis = 0L;
        ConcurrentHashMap<Integer, Long> concurrentHashMap = null;
        Long l = null;
        long j = 0L;
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(intent, "intent");
        try {
            IslandDispatcher islandDispatcher = this;
            fromIntent = IslandRequest.INSTANCE.fromIntent(intent);
            currentTimeMillis = System.currentTimeMillis();
            concurrentHashMap = lastPostByUid;
            l = concurrentHashMap.get(Integer.valueOf(uid));
            j = 0;
        } catch (Throwable th) {
            m5221constructorimpl = KResult.success(KResult.createFailure(th));
        }
        if (currentTimeMillis - (l != null ? l.longValue() : 0L) < 1000) {
            log(5, "rate limited uid=" + uid);
            return;
        }
        if (concurrentHashMap.size() >= 128) {
            concurrentHashMap.clear();
        }
        concurrentHashMap.put(Integer.valueOf(uid), Long.valueOf(currentTimeMillis));
        IslandNotification fromRequest = IslandNotification.INSTANCE.fromRequest(context, fromIntent);
        if (!fromIntent.getPersistent()) {
            j = fromIntent.getTimeoutMs();
        }
        IslandController.INSTANCE.showDispatched(fromRequest, j);
        log(3, "show id=" + fromIntent.getId() + " title=" + fromIntent.getTitle() + " uid=" + uid);
        m5221constructorimpl = KResult.success(Unit.INSTANCE);
        Throwable m5224exceptionOrNullimpl = KResult.exceptionOrNullimpl(m5221constructorimpl);
        if (m5224exceptionOrNullimpl != null) {
            INSTANCE.log(5, "onShow failed: " + m5224exceptionOrNullimpl.getMessage());
        }
    }

    public final void register(Context context, XposedModule module2) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(module2, "module");
        if (registered) {
            return;
        }
        synchronized (this) {
            if (registered) {
                return;
            }
            IslandDispatcher islandDispatcher = INSTANCE;
            appContext = context.getApplicationContext();
            module = module2;
            IslandDispatcherReceiver islandDispatcherReceiver = IslandDispatcherReceiver.INSTANCE;
            Context context2 = appContext;
            Intrinsics.checkNotNull(context2);
            islandDispatcherReceiver.register(context2);
            registered = true;
            islandDispatcher.log(4, "dispatcher registered");
            Unit unit = Unit.INSTANCE;
        }
    }
}
