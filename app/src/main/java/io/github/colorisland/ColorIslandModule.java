package io.github.colorisland;

import android.util.Log;
import io.github.colorisland.hook.SystemUIHook;
import io.github.colorisland.island.ChannelRegistry;
import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface;
import kotlin.Metadata;
import io.github.colorisland.util.KResult;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ColorIslandModule.kt */
/* loaded from: classes3.dex */
public final class ColorIslandModule extends XposedModule {
    public static final int $stable = 0;
    public static final String TAG = "ColorIsland";
    public static final String TARGET_PACKAGE = "com.android.systemui";

    private final void log(String message) {
        if (IslandConfig.INSTANCE.getDebugLog()) {
            try {
                ColorIslandModule colorIslandModule = this;
                log(3, "ColorIsland", message);
            } catch (Throwable th) {
            }
        }
    }

    private final void logError(String message) {
        try {
            ColorIslandModule colorIslandModule = this;
            log(6, "ColorIsland", message);
        } catch (Throwable th) {
        }
    }

    public void onPackageLoaded(XposedModuleInterface.PackageLoadedParam param) {
        Object m5221constructorimpl;
        Intrinsics.checkNotNullParameter(param, "param");
        Log.i("ColorIsland", "onPackageLoaded: " + param.getPackageName());
        if (Intrinsics.areEqual(param.getPackageName(), TARGET_PACKAGE)) {
            try {
                ColorIslandModule colorIslandModule = this;
                IslandConfig.INSTANCE.init(this);
                ChannelRegistry.INSTANCE.init(this);
                SystemUIHook.INSTANCE.install(this, param);
                Log.i("ColorIsland", "hooked into com.android.systemui");
                log("ColorIsland hooked into com.android.systemui");
                m5221constructorimpl = KResult.success(Unit.INSTANCE);
            } catch (Throwable th) {
                m5221constructorimpl = KResult.success(KResult.createFailure(th));
            }
            Throwable m5224exceptionOrNullimpl = KResult.exceptionOrNullimpl(m5221constructorimpl);
            if (m5224exceptionOrNullimpl != null) {
                Log.e("ColorIsland", "init failed", m5224exceptionOrNullimpl);
                logError("ColorIsland init failed: " + m5224exceptionOrNullimpl.getMessage());
            }
        }
    }
}
