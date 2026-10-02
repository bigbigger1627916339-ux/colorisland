package io.github.colorisland.island;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import androidx.core.app.NotificationCompat;
import io.github.colorisland.IslandConfig;
import io.github.colorisland.island.IslandSystemStatus;
import io.github.colorisland.island.IslandView;
import kotlin.Metadata;
import io.github.colorisland.util.KResult;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;

/* compiled from: IslandWindow.kt */
/* loaded from: classes3.dex */
public final class IslandWindow implements IslandView.Listener {
    private static final String TAG = "ColorIslandWindow";
    private final Context context;
    private IslandNotification currentNotif;
    private IslandSystemStatus currentStatus;
    private final float density;
    private IslandView islandView;
    private WindowManager.LayoutParams layoutParams;
    private FrameLayout rootView;
    private Animator sizeAnimator;
    private int statusBarHeightPx;
    private final WindowManager windowManager;
    public static final int $stable = 8;

    public IslandWindow(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.windowManager = (WindowManager) context.getSystemService(WindowManager.class);
        this.density = context.getResources().getDisplayMetrics().density;
        int identifier = this.context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        this.statusBarHeightPx = identifier > 0 ? this.context.getResources().getDimensionPixelSize(identifier) : (int) (28 * this.density);
    }

    private final void applySize(final int width, final int height, boolean animate) {
        final IslandView islandView = this.islandView;
        if (islandView == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = islandView.getLayoutParams();
        final FrameLayout.LayoutParams layoutParams2 = layoutParams instanceof FrameLayout.LayoutParams ? (FrameLayout.LayoutParams) layoutParams : null;
        if (layoutParams2 == null) {
            return;
        }
        if (!animate) {
            if (layoutParams2.width == width && layoutParams2.height == height) {
                return;
            }
            layoutParams2.width = width;
            layoutParams2.height = height;
            islandView.requestLayout();
            syncWindowSize();
            return;
        }
        final int i = layoutParams2.width;
        final int i2 = layoutParams2.height;
        if (i == width && i2 == height) {
            return;
        }
        Animator animator = this.sizeAnimator;
        if (animator != null) {
            animator.cancel();
        }
        layoutParams2.width = width;
        layoutParams2.height = height;
        syncWindowSize();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        ofFloat.setDuration(300L);
        ofFloat.setInterpolator(new DecelerateInterpolator(1.2f));
        final Ref.IntRef intRef = new Ref.IntRef();
        intRef.element = -1;
        final Ref.IntRef intRef2 = new Ref.IntRef();
        intRef2.element = -1;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: io.github.colorisland.island.IslandWindow$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                IslandWindow.applySize$lambda$13$lambda$12(i, width, i2, height, intRef, intRef2, layoutParams2, islandView, valueAnimator);
            }
        });
        this.sizeAnimator = ofFloat;
        ofFloat.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void applySize$lambda$13$lambda$12(int i, int i2, int i3, int i4, Ref.IntRef intRef, Ref.IntRef intRef2, FrameLayout.LayoutParams layoutParams, IslandView islandView, ValueAnimator a) {
        Intrinsics.checkNotNullParameter(a, "a");
        Object animatedValue = a.getAnimatedValue();
        Intrinsics.checkNotNull(animatedValue, "null cannot be cast to non-null type kotlin.Float");
        float floatValue = ((Float) animatedValue).floatValue();
        int i5 = (int) (i + ((i2 - i) * floatValue));
        int i6 = (int) (i3 + ((i4 - i3) * floatValue));
        if (i5 == intRef.element && i6 == intRef2.element) {
            return;
        }
        intRef.element = i5;
        intRef2.element = i6;
        layoutParams.width = i5;
        layoutParams.height = i6;
        islandView.requestLayout();
    }

    private final int defaultY(int offsetDp) {
        return this.statusBarHeightPx + dp(6.0f) + dp(offsetDp);
    }

    static /* synthetic */ int defaultY$default(IslandWindow islandWindow, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = IslandConfig.INSTANCE.getYOffsetDp();
        }
        return islandWindow.defaultY(i);
    }

    private final int dp(float value) {
        return (int) ((value * this.density) + 0.5f);
    }

    private final IslandView ensureView() {
        Object m5221constructorimpl;
        IslandView islandView = this.islandView;
        if (islandView != null) {
            return islandView;
        }
        IslandView islandView2 = new IslandView(this.context);
        islandView2.setListener(this);
        islandView2.setSizeChangeListener(new Function3() { // from class: io.github.colorisland.island.IslandWindow$$ExternalSyntheticLambda2
            @Override // kotlin.jvm.functions.Function3
            public final Object invoke(Object obj, Object obj2, Object obj3) {
                Unit ensureView$lambda$2;
                ensureView$lambda$2 = IslandWindow.ensureView$lambda$2(IslandWindow.this, ((Integer) obj).intValue(), ((Integer) obj2).intValue(), ((Boolean) obj3).booleanValue());
                return ensureView$lambda$2;
            }
        });
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 17;
        FrameLayout frameLayout = new FrameLayout(this.context);
        frameLayout.setClipChildren(false);
        frameLayout.setClipToPadding(false);
        frameLayout.setClickable(false);
        frameLayout.setFocusable(false);
        frameLayout.setLongClickable(false);
        frameLayout.setForeground(null);
        frameLayout.setBackground(null);
        frameLayout.setFitsSystemWindows(false);
        frameLayout.addView(islandView2, layoutParams);
        int[] iArr = {2024, 2019, 2038, 2006, 2014, 2005};
        int i = 0;
        Throwable th = null;
        for (int i2 = 6; i < i2; i2 = 6) {
            int i3 = iArr[i];
            WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams(-2, -2, i3, 16778024, -3);
            layoutParams2.gravity = 49;
            layoutParams2.x = 0;
            layoutParams2.y = defaultY$default(this, 0, 1, null);
            layoutParams2.windowAnimations = 0;
            layoutParams2.layoutInDisplayCutoutMode = 3;
            try {
                IslandWindow islandWindow = this;
                this.windowManager.addView(frameLayout, layoutParams2);
                this.layoutParams = layoutParams2;
                this.rootView = frameLayout;
                this.islandView = islandView2;
                m5221constructorimpl = KResult.success(Unit.INSTANCE);
            } catch (Throwable th2) {
                m5221constructorimpl = KResult.success(KResult.createFailure(th2));
            }
            if (KResult.isSuccessimpl(m5221constructorimpl)) {
                Log.i(TAG, "overlay window added type=" + i3 + " (fullscreen root)");
                return islandView2;
            }
            Throwable m5224exceptionOrNullimpl = KResult.exceptionOrNullimpl(m5221constructorimpl);
            Log.w(TAG, "addView failed type=" + i3 + ": " + (m5224exceptionOrNullimpl != null ? m5224exceptionOrNullimpl.getMessage() : null));
            try {
                IslandWindow islandWindow2 = this;
                this.windowManager.removeView(frameLayout);
            } catch (Throwable th3) {
            }
            i++;
            th = m5224exceptionOrNullimpl;
        }
        Log.e(TAG, "addView failed for all types", th);
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ensureView$lambda$2(IslandWindow islandWindow, int i, int i2, boolean z) {
        islandWindow.applySize(i, i2, z);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit hide$lambda$11(IslandWindow islandWindow, FrameLayout frameLayout) {
        if (islandWindow.rootView != frameLayout) {
            return Unit.INSTANCE;
        }
        try {
            islandWindow.windowManager.removeView(frameLayout);
        } catch (Throwable th) {
        }
        islandWindow.islandView = null;
        islandWindow.rootView = null;
        islandWindow.layoutParams = null;
        return Unit.INSTANCE;
    }

    private final void setTouchable(boolean touchable) {
        WindowManager.LayoutParams layoutParams;
        Object m5221constructorimpl;
        FrameLayout frameLayout = this.rootView;
        if (frameLayout == null || (layoutParams = this.layoutParams) == null) {
            return;
        }
        int i = layoutParams.flags;
        int i2 = touchable ? i & (-17) : i | 16;
        if (i2 == layoutParams.flags) {
            return;
        }
        layoutParams.flags = i2;
        try {
            IslandWindow islandWindow = this;
            this.windowManager.updateViewLayout(frameLayout, layoutParams);
            m5221constructorimpl = KResult.success(Unit.INSTANCE);
        } catch (Throwable th) {
            m5221constructorimpl = KResult.success(KResult.createFailure(th));
        }
        Throwable m5224exceptionOrNullimpl = KResult.exceptionOrNullimpl(m5221constructorimpl);
        if (m5224exceptionOrNullimpl != null) {
            Log.w(TAG, "setTouchable(" + touchable + ") failed: " + m5224exceptionOrNullimpl.getMessage());
        }
    }

    private final void syncWindowSize() {
        WindowManager.LayoutParams layoutParams;
        FrameLayout frameLayout = this.rootView;
        if (frameLayout == null || (layoutParams = this.layoutParams) == null) {
            return;
        }
        try {
            IslandWindow islandWindow = this;
            this.windowManager.updateViewLayout(frameLayout, layoutParams);
        } catch (Throwable th) {
        }
    }

    public final void applyColors() {
        IslandView islandView = this.islandView;
        if (islandView != null) {
            islandView.applyColors();
        }
    }

    public final void applyCutoutSpacer() {
        IslandView islandView = this.islandView;
        if (islandView != null) {
            islandView.refreshCutout();
        }
        IslandView islandView2 = this.islandView;
        if (islandView2 != null) {
            islandView2.applyCutoutSpacer();
        }
    }

    public final void applyYOffset(int yDp) {
        WindowManager.LayoutParams layoutParams;
        int defaultY;
        FrameLayout frameLayout = this.rootView;
        if (frameLayout == null || (layoutParams = this.layoutParams) == null || (defaultY = defaultY(yDp)) == layoutParams.y) {
            return;
        }
        layoutParams.y = defaultY;
        try {
            IslandWindow islandWindow = this;
            this.windowManager.updateViewLayout(frameLayout, layoutParams);
        } catch (Throwable th) {
        }
        Log.i(TAG, "applyYOffset y=" + yDp + "dp -> " + defaultY + "px");
    }

    public final void bind(IslandNotification notif, boolean expand) {
        Intrinsics.checkNotNullParameter(notif, "notif");
        this.currentNotif = notif;
        this.currentStatus = null;
        IslandView islandView = this.islandView;
        if (islandView != null) {
            islandView.bind(notif, expand);
        }
    }

    public final void hide() {
        final FrameLayout frameLayout;
        IslandView islandView = this.islandView;
        if (islandView == null || (frameLayout = this.rootView) == null) {
            return;
        }
        this.currentNotif = null;
        this.currentStatus = null;
        setTouchable(false);
        islandView.animateOut(new Function0() { // from class: io.github.colorisland.island.IslandWindow$$ExternalSyntheticLambda1
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                Unit hide$lambda$11;
                hide$lambda$11 = IslandWindow.hide$lambda$11(IslandWindow.this, frameLayout);
                return hide$lambda$11;
            }
        });
    }

    @Override // io.github.colorisland.island.IslandView.Listener
    public void onAction(IslandAction action) {
        Intrinsics.checkNotNullParameter(action, "action");
        IslandController.INSTANCE.runAction(action);
    }

    @Override // io.github.colorisland.island.IslandView.Listener
    public void onDismiss() {
        IslandController.INSTANCE.dismissCurrent();
    }

    @Override // io.github.colorisland.island.IslandView.Listener
    public void onOpen(IslandNotification notif) {
        Intrinsics.checkNotNullParameter(notif, "notif");
        IslandController.INSTANCE.openNotification(notif);
    }

    @Override // io.github.colorisland.island.IslandView.Listener
    public void onSeek(float fraction) {
        IslandNotification notif;
        IslandView islandView = this.islandView;
        if (islandView == null || (notif = islandView.getNotif()) == null) {
            return;
        }
        IslandController.INSTANCE.seekNotification(notif, fraction);
    }

    @Override // io.github.colorisland.island.IslandView.Listener
    public void onSystemStatusAction(IslandSystemStatus.Type type) {
        Intrinsics.checkNotNullParameter(type, "type");
        IslandController.INSTANCE.toggleSystemStatusAction(type);
    }

    @Override // io.github.colorisland.island.IslandView.Listener
    public void onToggleExpand() {
        IslandController.INSTANCE.toggleExpand();
    }

    public final void setExpanded(boolean expand) {
        IslandView islandView = this.islandView;
        if (islandView != null) {
            islandView.setExpanded(expand, true);
        }
    }

    public final void show(IslandNotification notif, boolean expand) {
        Intrinsics.checkNotNullParameter(notif, "notif");
        IslandView ensureView = ensureView();
        if (ensureView == null) {
            Log.e(TAG, "show failed: ensureView returned null");
            return;
        }
        setTouchable(true);
        this.currentNotif = notif;
        this.currentStatus = null;
        ensureView.bind(notif, expand);
        ensureView.animateIn();
    }

    public final void showStatus(IslandSystemStatus status, boolean expand) {
        Intrinsics.checkNotNullParameter(status, "status");
        this.currentStatus = status;
        this.currentNotif = null;
        IslandView islandView = this.islandView;
        if (islandView != null && islandView.getVisibility() == 0) {
            islandView.bindStatus(status, expand);
            return;
        }
        IslandView ensureView = ensureView();
        if (ensureView == null) {
            Log.e(TAG, "showStatus failed: ensureView returned null");
            return;
        }
        setTouchable(true);
        ensureView.bindStatus(status, expand);
        ensureView.animateIn();
    }

    public final void updateConnInfo(String info) {
        Intrinsics.checkNotNullParameter(info, "info");
        IslandView islandView = this.islandView;
        if (islandView != null) {
            islandView.updateConnInfo(info);
        }
    }
}
