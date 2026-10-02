package io.github.colorisland.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * ChannelItem 数据类单元测试（纯 JVM，JUnit 5）。
 *
 * ChannelItem 原为 Kotlin data class（经 JADX 转 Java），需验证其
 * 数据类语义（equals/hashCode/copy/toString）在移植后保持完整。
 */
@DisplayName("ChannelItem 渠道数据项")
class ChannelItemTest {

    @Test
    @DisplayName("构造与读取：三个字段正确存取")
    fun `construct and read fields`() {
        val item = ChannelItem("message", "消息", 12)

        assertEquals("message", item.id)
        assertEquals("消息", item.name)
        assertEquals(12, item.count)
    }

    @Test
    @DisplayName("相等性：同字段值相等，不同字段值不等")
    fun `equals contract`() {
        val a = ChannelItem("id1", "名称", 5)
        val b = ChannelItem("id1", "名称", 5)
        val c = ChannelItem("id2", "名称", 5)

        assertEquals(a, b, "同字段值应相等")
        assertEquals(a.hashCode(), b.hashCode(), "相等对象哈希必须一致")
        assertNotEquals(a, c, "id 不同应不相等")
        assertNotEquals(a, ChannelItem("id1", "名称", 6), "count 不同应不相等")
        assertNotEquals(a, ChannelItem("id1", "其他", 5), "name 不同应不相等")
    }

    @Test
    @DisplayName("copy：按需修改单个字段，其余保持不变")
    fun `copy modifies only target field`() {
        val original = ChannelItem("id1", "名称", 5)

        // ChannelItem 为 Java 类（Kotlin data class 反编译），copy 仅支持位置参数
        val updated = original.copy("id1", "名称", 9)

        assertEquals("id1", updated.id, "copy 后 id 应保持")
        assertEquals("名称", updated.name, "copy 后 name 应保持")
        assertEquals(9, updated.count, "copy 后 count 应为新值")
        assertEquals(5, original.count, "原对象不应被修改")
    }

    @Test
    @DisplayName("toString：包含全部字段值")
    fun `toString contains all fields`() {
        val text = ChannelItem("cid", "渠道名", 7).toString()

        assert(text.contains("cid"))
        assert(text.contains("渠道名"))
        assert(text.contains("7"))
    }

    @Test
    @DisplayName("空串边界：id/name 允许为空串（解析降级场景）")
    fun `empty strings are allowed`() {
        val item = ChannelItem("", "", 0)

        assertEquals("", item.id)
        assertEquals("", item.name)
        assertEquals(0, item.count)
    }

    @Test
    @DisplayName("null 防护：id/name 为 null 时抛出异常（Kotlin 非空契约）")
    fun `null fields throw`() {
        // Kotlin data class 的 Intrinsics.checkNotNullParameter 在移植后仍生效
        assertThrows(Exception::class.java) { ChannelItem(null, "名称", 1) }
        assertThrows(Exception::class.java) { ChannelItem("id", null, 1) }
    }
}
