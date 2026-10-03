import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// ===== 版本号自动递增机制 =====
// 规则：每次 assemble* 构建成功后 versionCode +1 并写回 version.properties；
//       versionName 与 versionCode 联动为 "1.0.<versionCode>"，保证单调递增、覆盖安装可靠。
//       clean / test / lint 等不含 assemble 的构建不触发递增。
val versionPropsFile = rootProject.file("version.properties")
val versionProps = Properties().apply {
    if (versionPropsFile.exists()) {
        versionPropsFile.inputStream().use { load(it) }
    }
}
val currentVersionCode: Int = versionProps.getProperty("versionCode", "1").toInt()

// 版本号递增标志：保证一次构建（即使含多个 assemble 任务）只递增一次
var versionBumped = false

// 任务图就绪后，给所有 assemble 任务注册 doLast：
// doLast 仅在该任务成功执行后运行，失败则不运行，天然满足"构建成功后递增"语义。
// 注意：必须用 addTaskExecutionGraphListener（Java SAM 接口）；
// 直接调 taskGraph.whenReady { } 会被解析到 Groovy Closure 重载，Kotlin 无法推断参数类型。
gradle.taskGraph.addTaskExecutionGraphListener { graph ->
    graph.allTasks
        .filter { task ->
            task.name.startsWith("assemble") && !task.name.contains("Clean", ignoreCase = true)
        }
        .forEach { task ->
            task.doLast {
                if (!versionBumped) {
                    versionBumped = true
                    versionProps.setProperty("versionCode", (currentVersionCode + 1).toString())
                    versionPropsFile.outputStream().use { out ->
                        versionProps.store(
                            out,
                            "Auto-incremented by app/build.gradle.kts on successful assemble",
                        )
                    }
                }
            }
        }
}

android {
    namespace = "io.github.colorisland"
    compileSdk = 37

    defaultConfig {
        applicationId = "io.github.colorisland"
        minSdk = 33
        targetSdk = 37
        // 版本号由 version.properties 驱动，每次成功构建后自动 +1
        versionCode = currentVersionCode
        versionName = "1.0.$currentVersionCode"
        // 只保留中文（瘦身：移除其他 80+ 语言资源）
        resourceConfigurations += setOf("zh-rCN")
    }

    signingConfigs {
        // 直接使用 debug 签名（用户要求），后续需要正式签名再替换
        getByName("debug") {
            // 默认 ~/.android/debug.keystore，由 AGP 自动生成
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // release 用 debug 签名，保证可直接安装
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            // 保留 Xposed 模块元数据，其余 META-INF 杂项全部剔除（瘦身）
            merges += "META-INF/xposed/**"
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE*",
                "META-INF/NOTICE*",
                "META-INF/*.version",
                "META-INF/*.kotlin_module",
                "kotlin-tooling-metadata.json",
                "DebugProbesKt.bin",
                "**/*.kotlin_builtins",
                "**/*.proto"
            )
        }
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

    testOptions {
        // JVM 单元测试缺少 Android 框架时返回默认值而非崩溃
        unitTests.isReturnDefaultValues = true
        // 启用 JUnit 5 平台（AGP 默认仅支持 JUnit 4）
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    // ---- Xposed 模块 API（编译期可见，运行期由 LSPosed 提供）----
    compileOnly("io.github.libxposed:api:102.0.0")
    implementation("io.github.libxposed:service:102.0.0")

    // ---- 单元测试（JVM 级，JUnit 5）----
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    // JUnit Platform Launcher：Gradle 执行 JUnit 5 测试的必需运行时组件
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // org.json 真实 JVM 实现：android.jar 中的 org.json 是抛 "Stub!" 的桩类
    testImplementation("org.json:json:20240303")

    // ---- AndroidX 基础 ----
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.3")
    // Miuix WindowListPopup 从 ViewTree 读取 NavigationEventDispatcherOwner，
    // 需显式依赖以在编译期引用（miuix 仅传递依赖，不暴露编译类路径）
    implementation("androidx.navigationevent:navigationevent:1.1.2")
    // navigationevent 的 ViewTreeNavigationEventDispatcherOwner 引用
    // androidx.core.viewtree.ViewTree，而 core:1.13.1 未传递该工件，需显式声明
    implementation("androidx.core:core-viewtree:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-process:2.8.7")

    // ---- Compose（与 miuix 0.9.4 对齐的 1.12.0）----
    implementation(platform("androidx.compose:compose-bom:2026.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.material3:material3")

    // ---- Miuix 0.9.4（MIUIx 风格组件库）----
    implementation("top.yukonga.miuix.kmp:miuix-ui-android:0.9.4")
    implementation("top.yukonga.miuix.kmp:miuix-preference-android:0.9.4")
    implementation("top.yukonga.miuix.kmp:miuix-icons-android:0.9.4")
    implementation("top.yukonga.miuix.kmp:miuix-blur-android:0.9.4")
}
