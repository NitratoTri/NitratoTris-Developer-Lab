#!/usr/bin/env sh
set -eu

ROOT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
BUILD_DIR=$(mktemp -d)
trap 'rm -rf "$BUILD_DIR"' EXIT HUP INT TERM

javac --release 17 -Xlint:all -Werror -d "$BUILD_DIR" \
  "$ROOT_DIR/src/main/java/dev/nitratotri/algorithms/LruCache.java" \
  "$ROOT_DIR/src/test/java/dev/nitratotri/algorithms/LruCacheTest.java"

java -ea -cp "$BUILD_DIR" dev.nitratotri.algorithms.LruCacheTest
