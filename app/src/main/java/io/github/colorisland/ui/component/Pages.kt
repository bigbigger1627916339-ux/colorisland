package io.github.colorisland.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TopAppBar

/**
 * Miuix 风格页面骨架（移植自 HyperIsland 的 CollapsingPage 设计模式）。
 *
 * 与原版的差异：
 * - 去掉了毛玻璃顶栏（BarBlurHost / BlurredBar），那些是 HyperIsland 基于
 *   miuix-blur 自建的效果组件，移植成本高且与 ColorIsland 无关；
 * - 去掉了 scrollBehavior（miuix 0.9.4 未提供 HyperIsland 源码中引用的
 *   MiuixScrollBehavior，仅有 ExitUntilCollapsedScrollBehavior 且需要
 *   TopAppBarState，收益不明显），改用固定大标题顶栏，视觉仍是纯正 Miuix 风格。
 *
 * 结构：Scaffold( TopAppBar(title + largeTitle) ) { LazyColumn { content } }
 */
@Composable
fun CollapsingPage(
    title: String,
    subtitle: String = "",
    content: LazyListScope.() -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = title,
                largeTitle = title,
                subtitle = subtitle,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            // 页面水平/垂直留白，与 Miuix 设置页一致
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            content()
        }
    }
}

/** 设置分组小标题（Miuix 原生 SmallTitle 组件，主色小字号） */
@Composable
fun SectionTitle(text: String) {
    SmallTitle(
        text = text,
        modifier = Modifier.padding(top = 4.dp),
        insideMargin = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
    )
}

/** 设置分组卡片：圆角卡片承载多行设置项 */
@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
    ) {
        content()
    }
}
