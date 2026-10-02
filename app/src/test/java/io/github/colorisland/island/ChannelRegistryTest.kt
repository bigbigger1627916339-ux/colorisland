package io.github.colorisland.island

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentHashMap

/**
 * ChannelRegistry.getChannels 排序逻辑单元测试（纯 JVM，JUnit 5）。
 *
 * getChannels 是纯内存逻辑（读 ConcurrentHashMap 缓存 + 按 count 降序排序），
 * 不依赖 Android 框架；缓存由 Hook 在 SystemUI 进程通过 observe() 填充，
 * 测试中经反射直接写入，绕过 Android 依赖。
 */
@DisplayName("ChannelRegistry 渠道注册表")
class ChannelRegistryTest {

    /** 反射获取私有静态缓存字段 */
    @Suppress("UNCHECKED_CAST")
    private fun cache(): ConcurrentHashMap<String, Map<String, ChannelRegistry.ChannelMeta>> {
        val field = ChannelRegistry::class.java.getDeclaredField("cache")
        field.isAccessible = true
        return field.get(null) as ConcurrentHashMap<String, Map<String, ChannelRegistry.ChannelMeta>>
    }

    /** 每个用例前清空静态缓存，保证用例隔离 */
    @BeforeEach
    fun resetCache() {
        cache().clear()
    }

    @Test
    @DisplayName("未知包名：返回空列表")
    fun `unknown package returns empty`() {
        assertTrue(ChannelRegistry.INSTANCE.getChannels("com.unknown").isEmpty())
    }

    @Test
    @DisplayName("单包多渠道：按 count 降序排列")
    fun `channels sorted by count descending`() {
        val inner = ConcurrentHashMap<String, ChannelRegistry.ChannelMeta>()
        inner["low"] = ChannelRegistry.ChannelMeta("low", "少", 1, 1000L)
        inner["high"] = ChannelRegistry.ChannelMeta("high", "多", 99, 1000L)
        inner["mid"] = ChannelRegistry.ChannelMeta("mid", "中", 10, 1000L)
        cache()["com.test.app"] = inner

        val result = ChannelRegistry.INSTANCE.getChannels("com.test.app")

        assertEquals(3, result.size)
        assertEquals(listOf("high", "mid", "low"), result.map { it.channelId },
            "应按 count 降序：99 > 10 > 1")
    }

    @Test
    @DisplayName("多包隔离：各包渠道互不串扰")
    fun `packages are isolated`() {
        val a = ConcurrentHashMap<String, ChannelRegistry.ChannelMeta>()
        a["a1"] = ChannelRegistry.ChannelMeta("a1", "A1", 5, 1L)
        val b = ConcurrentHashMap<String, ChannelRegistry.ChannelMeta>()
        b["b1"] = ChannelRegistry.ChannelMeta("b1", "B1", 7, 1L)
        cache()["com.pkg.a"] = a
        cache()["com.pkg.b"] = b

        assertEquals(listOf("a1"), ChannelRegistry.INSTANCE.getChannels("com.pkg.a").map { it.channelId })
        assertEquals(listOf("b1"), ChannelRegistry.INSTANCE.getChannels("com.pkg.b").map { it.channelId })
    }

    @Test
    @DisplayName("空内表：包名存在但无渠道时返回空列表")
    fun `empty inner map returns empty list`() {
        cache()["com.empty.inner"] = ConcurrentHashMap()

        assertTrue(ChannelRegistry.INSTANCE.getChannels("com.empty.inner").isEmpty())
    }

    @Test
    @DisplayName("ChannelMeta 字段：构造值正确存取")
    fun `channel meta fields`() {
        val meta = ChannelRegistry.ChannelMeta("cid", "名称", 3, 12345L)

        assertEquals("cid", meta.channelId)
        assertEquals("名称", meta.name)
        assertEquals(3, meta.count)
        assertEquals(12345L, meta.lastSeen)
    }
}
