#!/usr/bin/env bash
# =========================================================
#  build.sh  –  MiniBank build & run script
#  Usage:
#    ./build.sh          → compile + run
#    ./build.sh jar      → compile + package jar
#    ./build.sh test     → compile + run JUnit tests
#    ./build.sh clean    → remove output directories
# =========================================================
set -e

# ── Auto-detect JAVA_HOME on macOS (JDK 17+) if not set ──
if [ -z "$JAVA_HOME" ]; then
  if [ -d "/Library/Java/JavaVirtualMachines/jdk-27.jdk/Contents/Home" ]; then
    export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-27.jdk/Contents/Home"
  fi
fi
JAVAC="${JAVA_HOME:+$JAVA_HOME/bin/}javac"
JAVA="${JAVA_HOME:+$JAVA_HOME/bin/}java"
JAR_CMD="${JAVA_HOME:+$JAVA_HOME/bin/}jar"

SRC_DIR="src"
TEST_DIR="test"
OUT_DIR="out"
JAR_FILE="minibank.jar"
JUNIT_JAR="lib/junit-platform-console-standalone.jar"
MAIN_CLASS="MiniBank"

# Collect all .java source files
SOURCES=$(find "$SRC_DIR" -name "*.java")

case "${1:-run}" in

  # -------------------------------------------------------
  clean)
    echo "[clean] Removing $OUT_DIR and $JAR_FILE …"
    rm -rf "$OUT_DIR" "$JAR_FILE"
    echo "[clean] Done."
    ;;

  # -------------------------------------------------------
  compile|run|jar)
    echo "[compile] Compiling sources …"
    mkdir -p "$OUT_DIR"
    "$JAVAC" -d "$OUT_DIR" --source-path "$SRC_DIR" $SOURCES
    echo "[compile] Done."

    if [ "${1}" = "jar" ]; then
      echo "[jar] Building $JAR_FILE …"
      echo "Main-Class: $MAIN_CLASS" > manifest.txt
      echo "Class-Path: ." >> manifest.txt
      "$JAR_CMD" cfm "$JAR_FILE" manifest.txt -C "$OUT_DIR" .
      rm manifest.txt
      echo "[jar] Done.  Run with:  $JAVA -jar $JAR_FILE"

    elif [ "${1:-run}" = "run" ]; then
      echo "[run] Starting MiniBank …"
      echo "──────────────────────────────────────────────"
      "$JAVA" -cp "$OUT_DIR" "$MAIN_CLASS"
    fi
    ;;

  # -------------------------------------------------------
  test)
    if [ ! -f "$JUNIT_JAR" ]; then
      echo "[test] ERROR: $JUNIT_JAR not found."
      echo "       Download junit-platform-console-standalone from:"
      echo "       https://mvnrepository.com/artifact/org.junit.platform/junit-platform-console-standalone"
      exit 1
    fi
    echo "[compile] Compiling sources + tests …"
    mkdir -p "$OUT_DIR"
    TEST_SOURCES=$(find "$TEST_DIR" -name "*.java")
    "$JAVAC" -cp "$JUNIT_JAR" -d "$OUT_DIR" --source-path "$SRC_DIR" $SOURCES $TEST_SOURCES
    echo "[test] Running JUnit tests …"
    "$JAVA" -cp "$OUT_DIR:$JUNIT_JAR" org.junit.platform.console.standalone.ConsoleLauncher \
         --scan-class-path --class-path "$OUT_DIR"
    ;;

  *)
    echo "Unknown command: $1"
    echo "Usage: $0 [clean|compile|run|jar|test]"
    exit 1
    ;;
esac
