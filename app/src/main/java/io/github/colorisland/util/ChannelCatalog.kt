package io.github.colorisland.util

import io.github.colorisland.data.ChannelItem
import org.json.JSONObject

/**
 * 渠道目录 JSON 解析器（从 AppsPage 提取的纯逻辑，便于 JVM 单元测试）。
 *
 * 数据来源：ChannelRegistry 持久化到 SharedPreferences 的 "channel_catalog"，
 * 由 Hook 在 SystemUI 进程写入，经 ConfigSyncApp 同步到本进程。
 *
 * JSON 结构：
 * ```json
 * {
 *   "com.tencent.mm": [
 *     {"id": "message", "name": "消息", "count": 12},
 *     {"id": "subscribe", "name": "订阅号", "count": 3}
 *   ]
 * }
 * ```
 *
 * 容错策略（与 v1.0 行为对齐）：
 * - raw 为空/空白 → 空 Map；
 * - JSON 语法错误 → 空 Map（绝不由解析异常导致页面崩溃）；
 * - 单个包名为非数组 → 跳过该包；
 * - 数组元素非对象 → 跳过该元素；
 * - 字段缺失 → optString/optInt 默认值（""/0）。
 */
object ChannelCatalog {

    /** 解析渠道目录 JSON；任何异常均降级为空 Map，保证 UI 层不崩溃 */
    fun parse(raw: String?): Map<String, List<ChannelItem>> {
        if (raw.isNullOrBlank()) return emptyMap()
        return try {
            val root = JSONObject(raw)
            buildMap {
                root.keys().forEach { pkg ->
                    val arr = root.optJSONArray(pkg) ?: return@forEach
                    val list = ArrayList<ChannelItem>(arr.length())
                    for (i in 0 until arr.length()) {
                        val obj = arr.optJSONObject(i) ?: continue
                        // ChannelItem 是 Java 类，构造函数仅支持位置参数
                        list.add(
                            ChannelItem(
                                obj.optString("id"),
                                obj.optString("name"),
                                obj.optInt("count"),
                            ),
                        )
                    }
                    put(pkg, list)
                }
            }
        } catch (e: Exception) {
            // 解析失败统一降级：调用方按"无渠道数据"渲染
            emptyMap()
        }
    }
}