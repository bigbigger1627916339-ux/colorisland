package io.github.colorisland.util

import io.github.colorisland.data.ChannelItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * ChannelCatalog JSON 解析器单元测试（纯 JVM，JUnit 5）。
 *
 * 覆盖：正常解析、空值降级、语法错误降级、结构容错（非数组/非对象元素）、
 * 字段缺失默认值、多包名、空数组。
 */
@DisplayName("ChannelCatalog 渠道目录 JSON 解析")
class ChannelCatalogTest {

    @Test
    @DisplayName("正常 JSON：解析出包名→渠道列表映射")
    fun `parse valid json`() {
        val raw = """
            {
              "com.tencent.mm": [
                {"id": "message", "name": "消息", "count": 12},
                {"id": "subscribe", "name": "订阅号", "count": 3}
              ]
            }
        """.trimIndent()

        val result = ChannelCatalog.parse(raw)

        assertEquals(1, result.size, "应解析出 1 个包名")
        val channels = result.getValue("com.tencent.mm")
        assertEquals(2, channels.size, "应有 2 个渠道")
        assertEquals(ChannelItem("message", "消息", 12), channels[0])
        assertEquals(ChannelItem("subscribe", "订阅号", 3), channels[1])
    }

    @Test
    @DisplayName("多包名 JSON：每个包名独立成表")
    fun `parse multiple packages`() {
        val raw = """
            {
              "com.a": [{"id": "c1", "name": "一", "count": 1}],
              "com.b": [{"id": "c2", "name": "二", "count": 2}]
            }
        """.trimIndent()

        val result = ChannelCatalog.parse(raw)

        assertEquals(2, result.size)
        assertEquals(listOf(ChannelItem("c1", "一", 1)), result["com.a"])
        assertEquals(listOf(ChannelItem("c2", "二", 2)), result["com.b"])
    }

    @Test
    @DisplayName("空数组：包名存在但渠道列表为空")
    fun `parse empty array keeps empty list`() {
        val raw = """{"com.empty": []}"""

        val result = ChannelCatalog.parse(raw)

        assertEquals(1, result.size)
        assertTrue(result.getValue("com.empty").isEmpty(), "空数组应解析为空列表")
    }

    @Test
    @DisplayName("null 与空白字符串：降级为空 Map")
    fun `parse null or blank returns empty`() {
        assertTrue(ChannelCatalog.parse(null).isEmpty())
        assertTrue(ChannelCatalog.parse("").isEmpty())
        assertTrue(ChannelCatalog.parse("   \n\t ").isEmpty())
    }

    @Test
    @DisplayName("语法错误 JSON：降级为空 Map 而非抛异常")
    fun `parse malformed json returns empty`() {
        assertTrue(ChannelCatalog.parse("{not valid json").isEmpty())
        assertTrue(ChannelCatalog.parse("[1,2,3]").isEmpty(), "顶层是数组而非对象应降级")
        assertTrue(ChannelCatalog.parse("null").isEmpty())
    }

    @Test
    @DisplayName("结构容错：包名对应非数组时跳过该包")
    fun `parse skips non array package`() {
        val raw = """
            {
              "com.good": [{"id": "c", "name": "n", "count": 1}],
              "com.bad": "not-an-array"
            }
        """.trimIndent()

        val result = ChannelCatalog.parse(raw)

        assertEquals(1, result.size, "非数组包名应被跳过")
        assertTrue(result.containsKey("com.good"))
    }

    @Test
    @DisplayName("结构容错：数组元素非对象时跳过该元素")
    fun `parse skips non object elements`() {
        val raw = """
            {
              "com.mixed": [
                "just-a-string",
                42,
                null,
                {"id": "ok", "name": "可用", "count": 5}
              ]
            }
        """.trimIndent()

        val result = ChannelCatalog.parse(raw)

        val channels = result.getValue("com.mixed")
        assertEquals(1, channels.size, "仅对象元素应被解析")
        assertEquals(ChannelItem("ok", "可用", 5), channels[0])
    }

    @Test
    @DisplayName("字段缺失：id/name 默认空串，count 默认 0")
    fun `parse missing fields use defaults`() {
        val raw = """{"com.partial": [{"id": "only-id"}]}"""

        val channels = ChannelCatalog.parse(raw).getValue("com.partial")

        assertEquals(1, channels.size)
        val item = channels[0]
        assertEquals("only-id", item.id)
        assertEquals("", item.name, "缺失 name 应为空串")
        assertEquals(0, item.count, "缺失 count 应为 0")
    }

    @Test
    @DisplayName("空 JSON 对象：返回空 Map")
    fun `parse empty object returns empty map`() {
        assertTrue(ChannelCatalog.parse("{}").isEmpty())
    }
}
