package io.github.colorisland.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Kotlin 标准库文本/集合操作的 Java 等价实现。
 *
 * 背景：jadx 反编译产物调用了 kotlin.text.StringsKt / kotlin.collections.CollectionsKt
 * 的 $default 合成方法，但这些方法在 Kotlin 2.4.20 stdlib 中位于包私有的
 * facade 类（StringsKt__StringsKt 等），无法从应用包访问。
 * 此处提供语义等价的公开实现。
 */
public final class StringUtils {
    private StringUtils() {
    }

    /** 等价 Kotlin: CharSequence.contains(other: CharSequence, ignoreCase: Boolean = false) */
    public static boolean contains(CharSequence text, CharSequence other) {
        if (text == null || other == null) {
            return false;
        }
        return text.toString().contains(other.toString());
    }

    /** 等价 Kotlin: String.startsWith(prefix: String, ignoreCase: Boolean = false) */
    public static boolean startsWith(String text, String prefix) {
        if (text == null || prefix == null) {
            return false;
        }
        return text.startsWith(prefix);
    }

    /**
     * 等价 Kotlin: CharSequence.split(vararg delimiters: Char, ignoreCase: Boolean = false, limit: Int = 0)
     * 注意：Kotlin 的 split 非正则、保留尾部空串；此处保持一致。
     */
    public static List<String> split(CharSequence text, char... delimiters) {
        List<String> result = new ArrayList<>();
        if (text == null || text.length() == 0) {
            return result;
        }
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            boolean isDelimiter = false;
            for (char d : delimiters) {
                if (c == d) {
                    isDelimiter = true;
                    break;
                }
            }
            if (isDelimiter) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        return result;
    }

    /** 等价 Kotlin: Iterable<T>.joinToString(separator: CharSequence = ", ") */
    public static String joinToString(Iterable<?> items, String separator) {
        if (items == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object item : items) {
            if (!first) {
                sb.append(separator);
            }
            sb.append(item);
            first = false;
        }
        return sb.toString();
    }
}
