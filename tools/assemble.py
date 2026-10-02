#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
ColorIsland 分块源码组装脚本。

背景：仓库中少量体量较大的反编译参考源码（IslandController / IslandView /
SystemUIHook / SystemStatusMonitor / PrefsRepository）受单次推送体积限制，
被按行边界无损切分为 tools/split/<flat>.partN。本脚本负责把它们拼回
app/src/main/java 下的正常路径，拼回过一次后产物与原始源码逐字节一致。

用法：
    python3 tools/assemble.py            # 组装（已存在且一致则跳过）
    python3 tools/assemble.py --check    # 只校验，不写文件
    python3 tools/assemble.py --force    # 强制重写

命名约定：
    flat 名 = 源码相对 app/src/main/java 的路径，把 "/" 换成 "."。
    例如 io/github/colorisland/island/IslandController.java
      -> io.github.colorisland.island.IslandController.java
    反向映射时剥掉末段的类名（假设类名自身不含 "."），再还原为目录分隔符。
"""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SPLIT_DIR = ROOT / "tools" / "split"
JAVA_ROOT = ROOT / "app" / "src" / "main" / "java"

PART_RE = re.compile(r"^(?P<flat>.+)\.part(?P<idx>\d+)$")


def natural_key(text: str) -> list:
    """把 part10 排到 part2 之后，避免字典序错位。"""
    return [int(c) if c.isdigit() else c for c in re.split(r"(\d+)", text)]


def flat_to_rel(flat: str) -> Path:
    """io.github.colorisland.island.IslandController.java -> io/github/.../IslandController.java"""
    if not flat.endswith(".java"):
        raise ValueError(f"仅支持 .java 分块，收到: {flat}")
    stem = flat[: -len(".java")]           # io.github...IslandController
    pkg, _, cls = stem.rpartition(".")     # pkg=io.github..., cls=IslandController
    if not pkg or not cls:
        raise ValueError(f"无法解析 flat 名: {flat}")
    return Path(pkg.replace(".", "/")) / f"{cls}.java"


def collect() -> dict[str, list[Path]]:
    """按 flat 名归集分块，块内按 part 序号自然排序。"""
    groups: dict[str, list[Path]] = {}
    if not SPLIT_DIR.is_dir():
        return groups
    for entry in sorted(SPLIT_DIR.iterdir(), key=lambda p: natural_key(p.name)):
        if not entry.is_file():
            continue
        m = PART_RE.match(entry.name)
        if not m:
            continue
        groups.setdefault(m.group("flat"), []).append(entry)
    for flat in groups:
        groups[flat].sort(key=lambda p: natural_key(p.name))
    return groups


def assemble(force: bool = False, check_only: bool = False) -> int:
    groups = collect()
    if not groups:
        print(f"[!] 未在 {SPLIT_DIR} 找到任何 .partN 分块，无需组装")
        return 0

    changed = skipped = failed = 0
    for flat, parts in sorted(groups.items()):
        rel = flat_to_rel(flat)
        dst = JAVA_ROOT / rel
        # 顺序读取并拼接；分块按行边界切分，直接字节拼接即可无损还原
        blob = b"".join(p.read_bytes() for p in parts)

        if dst.exists() and not force:
            if dst.read_bytes() == blob:
                print(f"[=] {rel} 已是最新（{len(parts)} 块，{len(blob)} 字节）")
                skipped += 1
                continue
            print(f"[~] {rel} 内容不一致，将重写（{len(parts)} 块，{len(blob)} 字节）")

        if check_only:
            print(f"[?] {rel} 待写入（--check 模式不落盘）")
            continue

        dst.parent.mkdir(parents=True, exist_ok=True)
        dst.write_bytes(blob)
        print(f"[+] {rel} <- {len(parts)} 块，{len(blob)} 字节")
        changed += 1

    print(
        f"\n完成：写入 {changed}，跳过 {skipped}，失败 {failed}，"
        f"共处理 {len(groups)} 个源码文件"
    )
    return 1 if failed else 0


def main() -> int:
    ap = argparse.ArgumentParser(description="ColorIsland 分块源码组装")
    ap.add_argument("--force", action="store_true", help="忽略一致性，强制重写")
    ap.add_argument("--check", action="store_true", help="只校验并预览，不写文件")
    args = ap.parse_args()
    try:
        return assemble(force=args.force, check_only=args.check)
    except Exception as exc:  # 组装失败必须显式报错，避免静默产出半成品
        print(f"[x] 组装失败: {exc}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    sys.exit(main())
