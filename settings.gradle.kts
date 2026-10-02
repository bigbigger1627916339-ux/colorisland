pluginManagement {
    repositories {
        // 阿里云 google 镜像（AGP、androidx）
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        // 阿里云 public 镜像（Kotlin、miuix、libxposed 等）
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        // 阿里云 gradle-plugin 镜像
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        google()
        mavenCentral()
    }
}

rootProject.name = "ColorIsland"
include(":app")
