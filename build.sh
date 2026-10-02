#!/bin/bash
# ColorIsland 一键构建脚本
# 特性：每次 assemble 成功后 versionCode 自动 +1（由 build.gradle.kts + version.properties 驱动）
set -e
cd "$(dirname "$0")"

# Gradle 可执行文件路径（按本机实际安装位置修改）
GRADLE=${GRADLE:-/opt/gradle-9.6.1/bin/gradle}
TMPDIR_LOCAL="$PWD/.gradle-tmp"

echo "=== 当前版本号（本次构建使用）==="
grep '^versionCode' version.properties

echo "=== 开始构建 release ==="
set +e
"$GRADLE" assembleRelease \
    --no-daemon \
    -Dorg.gradle.vfs.watch=false \
    -Djava.io.tmpdir="$TMPDIR_LOCAL" \
    --stacktrace > .gradle-build.log 2>&1
GRADLE_EXIT=$?
set -e

# 输出关键行（错误/BUILD 结果）
grep -E "error:|^e: |BUILD|FAILED" .gradle-build.log | sort -u | head -20

if [ $GRADLE_EXIT -ne 0 ]; then
    echo "=== 构建失败（exit=$GRADLE_EXIT），完整日志见 .gradle-build.log ==="
    exit $GRADLE_EXIT
fi

APK=app/build/outputs/apk/release/app-release.apk
if [ ! -f "$APK" ]; then
    echo "=== 构建失败：未找到 APK ==="
    exit 1
fi

echo "=== 构建成功 ==="
ls -la "$APK"
echo "=== APK 版本信息 ==="
/opt/android-sdk/build-tools/34.0.0/aapt2 dump badging "$APK" 2>/dev/null | grep -E "^package: name" | head -1
echo "=== 新版本号（已自动递增，供下次构建使用）==="
grep '^versionCode' version.properties
