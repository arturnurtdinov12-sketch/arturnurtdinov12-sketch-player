#!/usr/bin/env bash
# Replaces the bundled TDLib stub with a real TDLib build output.
#
# Usage:
#   scripts/setup-tdlib.sh <path-to-tdlib-android-build>
#
# Expected layout under <path-to-tdlib-android-build>:
#   src/main/java/org/drinkless/tdlib/Client.java
#   src/main/java/org/drinkless/tdlib/TdApi.java
#   libs/<abi>/libtdjni.so   (arm64-v8a, armeabi-v7a, x86_64, x86)
#
# After running this script, ./gradlew assembleDebug produces an APK that
# really connects to Telegram.

set -euo pipefail

if [[ $# -lt 1 ]]; then
  echo "usage: $0 <path-to-tdlib-android-build>" >&2
  exit 1
fi

SRC="$1"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
JAVA_DST="$ROOT/app/src/main/java/org/drinkless/tdlib"
JNI_DST="$ROOT/app/src/main/jniLibs"

if [[ ! -f "$SRC/src/main/java/org/drinkless/tdlib/TdApi.java" ]]; then
  echo "TdApi.java not found under $SRC/src/main/java/org/drinkless/tdlib" >&2
  exit 1
fi

mkdir -p "$JAVA_DST" "$JNI_DST"

cp "$SRC/src/main/java/org/drinkless/tdlib/TdApi.java" "$JAVA_DST/TdApi.java"
cp "$SRC/src/main/java/org/drinkless/tdlib/Client.java" "$JAVA_DST/Client.java"

for abi in arm64-v8a armeabi-v7a x86_64 x86; do
  if [[ -f "$SRC/libs/$abi/libtdjni.so" ]]; then
    mkdir -p "$JNI_DST/$abi"
    cp "$SRC/libs/$abi/libtdjni.so" "$JNI_DST/$abi/libtdjni.so"
    echo "✓ installed libtdjni.so for $abi"
  fi
done

echo
echo "TDLib installed. Now run: ./gradlew assembleDebug"
