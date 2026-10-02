# ========== Xposed 模块入口（java_init.list 引用，绝不能混淆）==========
-keep class io.github.colorisland.ColorIslandModule { *; }
-keep class io.github.libxposed.** { *; }
-dontwarn io.github.libxposed.**

# ========== 模块间/反射访问的配置与数据类 ==========
-keep class io.github.colorisland.IslandConfig { *; }
-keep class io.github.colorisland.island.** { *; }
-keep class io.github.colorisland.hook.** { *; }
-keep class io.github.colorisland.data.** { *; }

# ========== Compose / Kotlin 元数据 ==========
-dontwarn org.jetbrains.compose.**
-dontwarn top.yukonga.miuix.kmp.**
-keep class top.yukonga.miuix.kmp.** { *; }

# Kotlin 协程
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }

# ========== 通用 ==========
-keepattributes *Annotation*, InnerClasses, Signature, Exceptions
-keep class org.json.** { *; }
