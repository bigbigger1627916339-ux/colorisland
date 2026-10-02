package io.github.colorisland.util;

/**
 * kotlin.Result 内联类的 Java 兼容实现。
 *
 * 背景：jadx 反编译 Kotlin runCatching{} 时生成了对 kotlin.Result 内部 API
 * （constructor-impl / isFailureimpl / exceptionOrNullimpl 等）的调用，
 * 这些 API 在 Java 层不可见（内联类无静态成员），导致编译失败。
 *
 * 设计：成功值保持原样不装箱（与 Kotlin 内联类运行时行为一致），
 * 失败值包装为 Failure 对象，从而兼容所有反编译模式：
 *   - runCatching{}.getOrNull()      → isFailureimpl 后置 null
 *   - runCatching{}.getOrDefault(x)  → isFailureimpl 后置默认值
 *   - runCatching{}.onFailure{}      → exceptionOrNullimpl 提取异常
 *   - runCatching{}（丢弃结果）       → 语句形式直接移除
 */
public final class KResult {
    private KResult() {
    }

    /** 失败包装：仅用于标记失败状态并携带异常 */
    public static final class Failure {
        public final Throwable error;

        public Failure(Throwable error) {
            this.error = error;
        }
    }

    /** 对应 Result.constructor-impl：成功值原样返回（恒等，泛型以保持调用点类型） */
    @SuppressWarnings("unchecked")
    public static <T> T success(T value) {
        return value;
    }

    /** 对应 ResultKt.createFailure：包装为失败标记 */
    public static Object createFailure(Throwable t) {
        return new Failure(t);
    }

    /** 对应 Result.isFailure-impl */
    public static boolean isFailureimpl(Object box) {
        return box instanceof Failure;
    }

    /** 对应 Result.isSuccess-impl */
    public static boolean isSuccessimpl(Object box) {
        return !(box instanceof Failure);
    }

    /** 对应 Result.exceptionOrNull-impl：失败时返回异常，否则 null */
    public static Throwable exceptionOrNullimpl(Object box) {
        return box instanceof Failure ? ((Failure) box).error : null;
    }

    /** 对应 Result.box-impl：值已是最终形态，恒等返回（泛型以保持调用点类型） */
    @SuppressWarnings("unchecked")
    public static <T> T boximpl(T box) {
        return box;
    }

    /** 对应 ResultKt.throwOnFailure：失败则重新抛出 */
    public static void throwOnFailure(Object box) {
        Throwable t = exceptionOrNullimpl(box);
        if (t != null) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            if (t instanceof Error) {
                throw (Error) t;
            }
            throw new RuntimeException(t);
        }
    }
}
