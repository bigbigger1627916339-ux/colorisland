package io.github.colorisland.island.dispatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import kotlin.jvm.internal.Intrinsics;

/**
 * 灵动岛广播接收器：接收第三方应用发送的 SHOW/CANCEL 广播，
 * 校验发送方持有 signature 级权限后转发给 IslandDispatcher。
 */
public final class IslandDispatcherReceiver {
    public static final IslandDispatcherReceiver INSTANCE = new IslandDispatcherReceiver();

    /** 广播接收器实例（匿名内部类，内联实现） */
    private static final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(intent, "intent");
            // 优先使用 ApplicationContext，避免 Activity 泄漏
            Context ctx = context.getApplicationContext();
            if (ctx == null) {
                ctx = context;
            }
            // API 34+ 可获取发送方 uid；低版本固定为 -1（仅依赖权限声明校验）
            int uid = -1;
            if (Build.VERSION.SDK_INT >= 34) {
                try {
                    uid = getSentFromUid();
                } catch (Throwable t) {
                    uid = -1;
                }
            }
            if (INSTANCE.isTrustedSender(ctx, uid)) {
                INSTANCE.dispatch(ctx, intent, uid);
            } else {
                IslandDispatcher.INSTANCE.logRejected(uid);
            }
        }
    };

    private IslandDispatcherReceiver() {
    }

    /** 按 action 分发到 IslandDispatcher */
    private final void dispatch(Context context, Intent intent, int uid) {
        String action = intent.getAction();
        if (action != null) {
            switch (action.hashCode()) {
                case -2117152520:
                    if (action.equals(IslandDispatchContract.ACTION_SHOW)) {
                        IslandDispatcher.INSTANCE.onShow(context, intent, uid);
                    }
                    break;
                case 537540251:
                    if (action.equals(IslandDispatchContract.ACTION_CANCEL)) {
                        IslandDispatcher.INSTANCE.onCancel(context, intent, uid);
                    }
                    break;
            }
        }
    }

    /** 发送方必须持有 signature 级 SEND_ISLAND 权限 */
    private final boolean isTrustedSender(Context context, int uid) {
        return uid >= 0 && context.checkPermission(IslandDispatchContract.PERMISSION, -1, uid) == 0;
    }

    /** 注册广播接收器（由 SystemUIHook 在 SystemUI 进程调用） */
    public final void register(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IslandDispatchContract.ACTION_SHOW);
        intentFilter.addAction(IslandDispatchContract.ACTION_CANCEL);
        // API 34+ 必须显式指定 exported 标志；低版本通过权限保护
        context.registerReceiver(receiver, intentFilter,
                Build.VERSION.SDK_INT >= 34 ? null : IslandDispatchContract.PERMISSION, null, 2);
    }
}
