package io.github.colorisland.data;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import io.github.colorisland.util.KResult;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;

/* compiled from: AppsRepository.kt */
/* loaded from: classes3.dex */
public final class AppsRepository {
    public static final int $stable = 0;
    private static final int ICON_TARGET_PX = 120;
    public static final AppsRepository INSTANCE = new AppsRepository();

    private AppsRepository() {
    }

    private final Drawable toScaledIcon(Context context, Drawable drawable) {
        Bitmap bitmap;
        if ((drawable instanceof BitmapDrawable) && (bitmap = ((BitmapDrawable) drawable).getBitmap()) != null && bitmap.getWidth() <= 240) {
            return drawable;
        }
        int coerceAtLeast = RangesKt.coerceAtLeast(drawable.getIntrinsicWidth(), 1);
        int coerceAtLeast2 = RangesKt.coerceAtLeast(drawable.getIntrinsicHeight(), 1);
        float max = 120.0f / Math.max(coerceAtLeast, coerceAtLeast2);
        int coerceAtLeast3 = RangesKt.coerceAtLeast((int) (coerceAtLeast * max), 1);
        int coerceAtLeast4 = RangesKt.coerceAtLeast((int) (coerceAtLeast2 * max), 1);
        Bitmap createBitmap = Bitmap.createBitmap(coerceAtLeast3, coerceAtLeast4, Bitmap.Config.ARGB_8888);
        Intrinsics.checkNotNullExpressionValue(createBitmap, "createBitmap(...)");
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, coerceAtLeast3, coerceAtLeast4);
        drawable.draw(canvas);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), createBitmap);
        bitmapDrawable.setBounds(0, 0, coerceAtLeast3, coerceAtLeast4);
        return bitmapDrawable;
    }

    public final List<AppEntry> loadLaunchableApps(Context context) {
        Object m5221constructorimpl;
        Object m5221constructorimpl2;
        Object m5221constructorimpl3;
        Intrinsics.checkNotNullParameter(context, "context");
        PackageManager packageManager = context.getPackageManager();
        Intent addCategory = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER");
        Intrinsics.checkNotNullExpressionValue(addCategory, "addCategory(...)");
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        try {
            AppsRepository appsRepository = this;
            m5221constructorimpl = KResult.success(packageManager.queryIntentActivities(addCategory, 0));
        } catch (Throwable th) {
            m5221constructorimpl = KResult.success(KResult.createFailure(th));
        }
        if (KResult.isFailureimpl(m5221constructorimpl)) {
            m5221constructorimpl = null;
        }
        List<ResolveInfo> list = (List) m5221constructorimpl;
        if (list != null) {
            for (ResolveInfo resolveInfo : list) {
                String str = resolveInfo.activityInfo.packageName;
                if (!Intrinsics.areEqual(str, context.getPackageName()) && !linkedHashMap.containsKey(str)) {
                    ApplicationInfo applicationInfo = resolveInfo.activityInfo.applicationInfo;
                    Intrinsics.checkNotNullExpressionValue(applicationInfo, "applicationInfo");
                    try {
                        m5221constructorimpl2 = KResult.success(resolveInfo.loadIcon(packageManager));
                    } catch (Throwable th2) {
                        m5221constructorimpl2 = KResult.success(KResult.createFailure(th2));
                    }
                    if (KResult.isFailureimpl(m5221constructorimpl2)) {
                        m5221constructorimpl2 = null;
                    }
                    Drawable drawable = (Drawable) m5221constructorimpl2;
                    LinkedHashMap linkedHashMap2 = linkedHashMap;
                    Intrinsics.checkNotNull(str);
                    try {
                        m5221constructorimpl3 = KResult.success(resolveInfo.loadLabel(packageManager).toString());
                    } catch (Throwable th3) {
                        m5221constructorimpl3 = KResult.success(KResult.createFailure(th3));
                    }
                    if (KResult.isFailureimpl(m5221constructorimpl3)) {
                        m5221constructorimpl3 = str;
                    }
                    Intrinsics.checkNotNullExpressionValue(m5221constructorimpl3, "getOrDefault(...)");
                    linkedHashMap2.put(str, new AppEntry(str, (String) m5221constructorimpl3, drawable != null ? INSTANCE.toScaledIcon(context, drawable) : null, (applicationInfo.flags & 1) != 0));
                }
            }
        }
        Collection values = linkedHashMap.values();
        Intrinsics.checkNotNullExpressionValue(values, "<get-values>(...)");
        return CollectionsKt.sortedWith(values, new Comparator<AppEntry>() { // from class: io.github.colorisland.data.AppsRepository$loadLaunchableApps$$inlined$sortedBy$1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.util.Comparator
            public final int compare(AppEntry t, AppEntry t2) {
                String lowerCase = t.getLabel().toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
                String lowerCase2 = t2.getLabel().toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue(lowerCase2, "toLowerCase(...)");
                return ComparisonsKt.compareValues(lowerCase, lowerCase2);
            }
        });
    }
}
