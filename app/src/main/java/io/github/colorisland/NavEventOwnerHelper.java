package io.github.colorisland;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigationevent.NavigationEventDispatcher;
import androidx.navigationevent.NavigationEventDispatcherOwner;
import androidx.navigationevent.ViewTreeNavigationEventDispatcherOwner;

/**
 * Miuix WindowListPopup 适配辅助类（Java 实现）。
 *
 * 背景：Miuix 的下拉弹窗在附加到窗口时会从 ViewTree 读取
 * NavigationEventDispatcherOwner，ComponentActivity 默认不提供，
 * 直接使用下拉组件会抛出 IllegalStateException。
 *
 * 实现说明：此处用 Java 而非 Kotlin，是因为当前构建环境中 Kotlin 编译器
 * 无法解析 ViewTreeNavigationEventDispatcherOwner 类（同类 jar 中其他类
 * 均可正常解析，疑似 kotlinc 在该环境的兼容性缺陷），javac 无此问题。
 */
public final class NavEventOwnerHelper {

    private NavEventOwnerHelper() {
    }

    /**
     * 在指定 View 上设置导航事件分发器 Owner。
     *
     * @param view 目标视图（通常为 Activity 的 DecorView）
     */
    public static void setOwner(@NonNull View view) {
        NavigationEventDispatcherOwner owner = new NavigationEventDispatcherOwner() {
            @Override
            @NonNull
            public NavigationEventDispatcher getNavigationEventDispatcher() {
                return new NavigationEventDispatcher();
            }
        };
        ViewTreeNavigationEventDispatcherOwner.set(view, owner);
        Log.d("ColorIslandNav", "setOwner called, view=" + view
                + ", verify=" + (ViewTreeNavigationEventDispatcherOwner.get(view) != null));
    }
}
