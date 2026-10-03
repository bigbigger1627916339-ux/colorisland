# ColorIsland

ColorOS 14 灵动岛 Xposed 模块 —— 自绘悬浮岛 + AOSP 通知 Hook，Miuix 风格界面。

## 功能特性

- **灵动岛悬浮窗**：自绘胶囊形悬浮岛，支持充电 / 手电筒 / 热点 / 录屏等状态展示
- **通知 Hook**：Hook SystemUI 通知分发，将通知浓缩为岛内条目，支持静默替换与频道管理
- **应用管理**：扫描已安装应用，按包名配置是否推送岛通知（`QUERY_ALL_PACKAGES`）
- **Miuix 风格 UI**：基于 Jetpack Compose + Miuix 0.9.4 的设置界面（首页 / 应用 / 行为三个页面）
- **跨进程通信**：自定义 `SEND_ISLAND` signature 级权限 + `IslandDispatcher` 广播分发，第三方应用可向岛推送内容

## 环境要求

| 项目 | 要求 |
|------|------|
| 系统 | ColorOS 14（Android 14+，minSdk 33） |
| 框架 | LSPosed / libXposed API 102 |
| Hook 作用域 | `com.android.systemui` |
| 构建 | JDK 17 + Gradle + Android SDK（compileSdk 37） |

## 构建

**首次构建前必须先还原源码。** 仓库中 5 个较大的源码文件受单文件上传体积限制，以分片形式存放在
`tools/split/` 目录，需先用组装脚本生成到 `app/src/main/java/`：

```bash
python3 tools/assemble.py           # 写入缺失/过期的源码文件
python3 tools/assemble.py --check   # 只校验一致性，不写入任何文件
python3 tools/assemble.py --force   # 强制重写全部源码文件
```

受此影响的文件（其余源码均为普通文件，无需处理）：

- `io/github/colorisland/data/PrefsRepository.java`
- `io/github/colorisland/hook/SystemUIHook.java`
- `io/github/colorisland/island/IslandController.java`
- `io/github/colorisland/island/IslandView.java`
- `io/github/colorisland/island/SystemStatusMonitor.java`

还原完成后即可正常构建：

```bash
# 方式一：一键脚本（versionCode 自动递增）
./build.sh

# 方式二：手动
gradle assembleRelease
```

产物位于 `app/build/outputs/apk/release/app-release.apk`。

> 注：release 默认使用 debug 签名便于直接安装，正式发布前请在 `app/build.gradle.kts` 中替换为自己的签名配置。

## 项目结构

```
app/src/main/
├── java/io/github/colorisland/
│   ├── ColorIslandModule.java      # Xposed 模块入口
│   ├── hook/SystemUIHook.java      # SystemUI Hook 逻辑（由 tools/split 分片组装）
│   ├── dispatch/                   # 跨进程岛消息分发（Contract/Dispatcher/Request）
│   ├── data/                       # 数据层（AppsRepository / PrefsRepository）
│   ├── island/                     # 灵动岛核心（Controller / View / SystemStatusMonitor）
│   ├── ui/                         # Compose UI（theme / page / component）
│   └── MainActivity.kt             # 设置界面入口
├── res/
│   ├── drawable/                   # 自适应图标三层 + 状态图标（全 VectorDrawable）
│   ├── mipmap-anydpi-v26/          # Adaptive Icon 入口
│   └── values/                     # strings / themes
└── resources/META-INF/xposed/      # Xposed 模块元数据（module.prop / scope.list）

tools/
├── split/                          # 大文件分片（<扁平类名>.partN，按序拼接即还原）
└── assemble.py                     # 分片 → app/src/main/java 组装脚本
```

## 依赖

- [libXposed API](https://github.com/libxposed/app) `102.0.0`（compileOnly）
- Jetpack Compose BOM `2026.01.00`
- [Miuix](https://github.com/miuix-kotlin-multiplatform/miuix) `0.9.4`

## 免责声明

本项目为系统级 Hook 模块，仅供学习与技术研究使用。Hook 系统应用存在稳定性风险，请自行承担使用后果。请勿用于任何违反当地法律法规的用途。

## License

待定（见仓库根目录 LICENSE 文件，尚未添加）。
