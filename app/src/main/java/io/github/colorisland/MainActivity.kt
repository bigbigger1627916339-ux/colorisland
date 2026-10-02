package io.github.colorisland

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

/**
 * 主界面入口：承载 Miuix 风格三页 UI（首页 / 应用 / 行为）。
 *
 * 配置仓库由 ConfigSyncApp（Application）提供，生命周期与应用进程一致，
 * 避免 Activity 重建时丢失配置状态。
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Miuix 的 WindowListPopup（下拉菜单/弹窗）在附加到窗口时，会从 ViewTree
        // 读取 NavigationEventDispatcherOwner 以接管返回手势。ComponentActivity
        // 默认不提供该 Owner，直接使用下拉组件会崩溃：
        //   IllegalStateException: No NavigationEventDispatcher was provided
        // 这里通过 Java 辅助类完成设置（Kotlin 侧无法直接引用该类，详见辅助类注释）。
        NavEventOwnerHelper.setOwner(window.decorView)

        // 从 Application 获取配置仓库（跨进程同步后的单一数据源）
        val prefsRepo = (application as ConfigSyncApp).prefsRepo
        setContent {
            ColorIslandApp(repo = prefsRepo)
        }
    }
}
