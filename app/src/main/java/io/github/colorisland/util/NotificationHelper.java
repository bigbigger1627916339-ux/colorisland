package io.github.colorisland.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import io.github.colorisland.MainActivity;
import io.github.colorisland.R;
import kotlin.Metadata;
import io.github.colorisland.util.KResult;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotificationHelper.kt */
/* loaded from: classes3.dex */
public final class NotificationHelper {
    public static final int $stable = 0;
    private static final String CHANNEL_ID = "colorisland_test";
    public static final NotificationHelper INSTANCE = new NotificationHelper();
    private static final int NOTIFICATION_ID = 17225;

    private NotificationHelper() {
    }

    public final boolean openLsposed(Context context) {
        Object m5221constructorimpl;
        Intrinsics.checkNotNullParameter(context, "context");
        Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage("org.lsposed.manager");
        if (launchIntentForPackage == null) {
            return false;
        }
        launchIntentForPackage.addFlags(268435456);
        try {
            NotificationHelper notificationHelper = this;
            context.startActivity(launchIntentForPackage);
            m5221constructorimpl = KResult.success(true);
        } catch (Throwable th) {
            m5221constructorimpl = KResult.success(KResult.createFailure(th));
        }
        if (KResult.isFailureimpl(m5221constructorimpl)) {
            m5221constructorimpl = false;
        }
        return ((Boolean) m5221constructorimpl).booleanValue();
    }

    public final boolean restartSystemUI() {
        Object m5221constructorimpl;
        try {
            NotificationHelper notificationHelper = this;
            boolean z = true;
            if (Runtime.getRuntime().exec(new String[]{"su", "-c", "am crash com.android.systemui"}).waitFor() != 0) {
                z = false;
            }
            m5221constructorimpl = KResult.success(Boolean.valueOf(z));
        } catch (Throwable th) {
            m5221constructorimpl = KResult.success(KResult.createFailure(th));
        }
        if (KResult.isFailureimpl(m5221constructorimpl)) {
            m5221constructorimpl = false;
        }
        return ((Boolean) m5221constructorimpl).booleanValue();
    }

    public final void sendTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "ColorIsland 测试", 4);
        notificationChannel.setDescription("用于验证灵动岛显示效果");
        notificationChannel.setShowBadge(false);
        notificationManager.createNotificationChannel(notificationChannel);
        PendingIntent activity = PendingIntent.getActivity(context, 0, new Intent(context, (Class<?>) MainActivity.class), 201326592);
        Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_stat_island).setContentTitle("ColorIsland 测试通知").setContentText("如果你看到了灵动岛，说明模块工作正常 ✨").setWhen(System.currentTimeMillis()).setAutoCancel(true).setContentIntent(activity).addAction(new Notification.Action.Builder((Icon) null, "打开应用", activity).build());
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
