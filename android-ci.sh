#!/bin/bash
set -e

PACKAGE_NAME="com.study.app"
APP_NAME="Study"
BUILD_DIR="./app/build"
DOWNLOAD_TYPE=""

# Proxy settings (modify if needed)
HTTPS_PROXY="${HTTPS_PROXY:-}"
HTTP_PROXY="${HTTP_PROXY:-}"
# Example: HTTPS_PROXY="socks5://127.0.0.1:7890"
# Example: HTTP_PROXY="socks5://127.0.0.1:7890"

function download_debug() {
    echo "=== Downloading latest debug APK from android-ci ==="
    DOWNLOAD_TYPE="debug"

    mkdir -p "$BUILD_DIR"

    # Find latest successful debug build via gh
    RUN_ID=$(gh run list --workflow android-ci.yml --status success --limit 1 --json databaseId --jq '.[0].databaseId')

    if [ -z "$RUN_ID" ] || [ "$RUN_ID" = "null" ]; then
        echo "No successful CI run found"
        exit 1
    fi

    echo "Found successful run: $RUN_ID"

    # Get artifact info
    ARTIFACT_JSON=$(gh api repos/yiranlaux/my-study/actions/runs/$RUN_ID/artifacts --jq '.artifacts[] | select(.name == "build-artifacts") | {id: .id, size: .size}')
    ARTIFACT_ID=$(echo "$ARTIFACT_JSON" | jq -r '.id')
    FILE_SIZE=$(echo "$ARTIFACT_JSON" | jq -r '.size')
    FILE_SIZE_MB=$((FILE_SIZE / 1024 / 1024))
    echo "Artifact size: ${FILE_SIZE_MB} MB"

    if [ -z "$ARTIFACT_ID" ] || [ "$ARTIFACT_ID" = "null" ]; then
        echo "Artifact not found"
        exit 1
    fi

    # Set proxy if configured
    if [ -n "$HTTPS_PROXY" ] || [ -n "$HTTP_PROXY" ]; then
        export HTTPS_PROXY
        export HTTP_PROXY
        echo "Using proxy: HTTPS_PROXY=$HTTPS_PROXY HTTP_PROXY=$HTTP_PROXY"
    fi

    # Clean up previous artifacts
    rm -f "$BUILD_DIR/build-artifacts.zip"
    rm -rf "$BUILD_DIR/outputs"
    rm -f "$BUILD_DIR/my-study-debug.apk"

    # Download zip artifact
    echo "Downloading..."
    curl -L -# -f -o "$BUILD_DIR/build-artifacts.zip" \
        -H "Authorization: Bearer $(gh auth token)" \
        "https://api.github.com/repos/yiranlaux/my-study/actions/artifacts/$ARTIFACT_ID/zip"

    # Extract zip with full overwrite coverage
    echo "Extracting..."
    unzip -o "$BUILD_DIR/build-artifacts.zip" -d "$BUILD_DIR"

    # Find and copy APK to target location
    APK_PATH=$(find "$BUILD_DIR" -name "*.apk" -type f | head -1)
    if [ -n "$APK_PATH" ] && [ -f "$APK_PATH" ]; then
        cp -f "$APK_PATH" "$BUILD_DIR/my-study-debug.apk"
        rm -f "$BUILD_DIR/build-artifacts.zip"
        rm -rf "$BUILD_DIR/outputs"
        echo "APK saved to: $BUILD_DIR/my-study-debug.apk"
    else
        echo "APK not found in artifacts"
        exit 1
    fi
}

function download_release() {
    echo "=== Downloading latest release APK from android-release ==="
    DOWNLOAD_TYPE="release"

    mkdir -p "$BUILD_DIR"

    # Find latest successful release build via gh
    RUN_ID=$(gh run list --workflow android-release.yml --status success --limit 1 --json databaseId --jq '.[0].databaseId')

    if [ -z "$RUN_ID" ] || [ "$RUN_ID" = "null" ]; then
        echo "No successful release run found"
        exit 1
    fi

    echo "Found successful run: $RUN_ID"

    # Get artifact info
    ARTIFACT_JSON=$(gh api repos/yiranlaux/my-study/actions/runs/$RUN_ID/artifacts --jq '.artifacts[] | select(.name == "release-apk") | {id: .id, size: .size}')
    ARTIFACT_ID=$(echo "$ARTIFACT_JSON" | jq -r '.id')
    FILE_SIZE=$(echo "$ARTIFACT_JSON" | jq -r '.size')
    FILE_SIZE_MB=$((FILE_SIZE / 1024 / 1024))
    echo "Artifact size: ${FILE_SIZE_MB} MB"

    if [ -z "$ARTIFACT_ID" ] || [ "$ARTIFACT_ID" = "null" ]; then
        echo "Artifact not found"
        exit 1
    fi

    # Set proxy if configured
    if [ -n "$HTTPS_PROXY" ] || [ -n "$HTTP_PROXY" ]; then
        export HTTPS_PROXY
        export HTTP_PROXY
        echo "Using proxy: HTTPS_PROXY=$HTTPS_PROXY HTTP_PROXY=$HTTP_PROXY"
    fi

    # Clean up previous artifacts
    rm -f "$BUILD_DIR/release-apk.zip"
    rm -rf "$BUILD_DIR/outputs"
    rm -f "$BUILD_DIR/my-study-release.apk"

    # Download zip artifact
    echo "Downloading..."
    curl -L -# -f -o "$BUILD_DIR/release-apk.zip" \
        -H "Authorization: Bearer $(gh auth token)" \
        "https://api.github.com/repos/yiranlaux/my-study/actions/artifacts/$ARTIFACT_ID/zip"

    # Extract zip with full overwrite coverage
    echo "Extracting..."
    unzip -o "$BUILD_DIR/release-apk.zip" -d "$BUILD_DIR"

    # Find and copy APK to target location
    APK_PATH=$(find "$BUILD_DIR" -name "*.apk" -type f | head -1)
    if [ -n "$APK_PATH" ] && [ -f "$APK_PATH" ]; then
        cp -f "$APK_PATH" "$BUILD_DIR/my-study-release.apk"
        rm -f "$BUILD_DIR/release-apk.zip"
        rm -rf "$BUILD_DIR/outputs"
        echo "APK saved to: $BUILD_DIR/my-study-release.apk"
    else
        echo "APK not found in artifacts"
        exit 1
    fi
}

function reinstall() {
    if [ "$DOWNLOAD_TYPE" = "release" ]; then
        APK_PATH="$BUILD_DIR/my-study-release.apk"
    elif [ "$DOWNLOAD_TYPE" = "debug" ]; then
        APK_PATH="$BUILD_DIR/my-study-debug.apk"
    else
        if [ -f "$BUILD_DIR/my-study-release.apk" ]; then
            DOWNLOAD_TYPE="release"
            APK_PATH="$BUILD_DIR/my-study-release.apk"
        elif [ -f "$BUILD_DIR/my-study-debug.apk" ]; then
            DOWNLOAD_TYPE="debug"
            APK_PATH="$BUILD_DIR/my-study-debug.apk"
        else
            echo "No APK found in $BUILD_DIR"
            echo "Run with -b or -br first"
            exit 1
        fi
    fi

    echo "=== Reinstalling $DOWNLOAD_TYPE APK ==="

    echo "Uninstalling existing package..."
    adb uninstall "$PACKAGE_NAME" 2>/dev/null || true

    echo "Installing APK..."
    adb install -r "$APK_PATH"

    echo "Launching $APP_NAME..."
    adb shell am start -n "$PACKAGE_NAME/.MainActivity"

    echo "Done!"
}

function view_logs() {
    echo "=== Viewing $APP_NAME logs (Ctrl+C to exit) ==="
    adb logcat -s "$PACKAGE_NAME":V
}

function show_help() {
    echo "Android CI Helper Script"
    echo ""
    echo "Usage: $0 [-b] [-br] [-r] [-l] [-h]"
    echo ""
    echo "Options:"
    echo "  -b    Download latest debug APK from android-ci"
    echo "  -br   Download latest release APK from android-release"
    echo "  -r    Reinstall downloaded APK (use after -b or -br)"
    echo "  -l    View logcat"
    echo "  -h    Show help"
    echo ""
    echo "Examples:"
    echo "  $0 -b       # Download debug APK"
    echo "  $0 -br      # Download release APK"
    echo "  $0 -b -r    # Download debug and reinstall"
    echo "  $0 -br -r   # Download release and reinstall"
}

if [ $# -eq 0 ]; then
    show_help
    exit 0
fi

while [ $# -gt 0 ]; do
    case "$1" in
        -b)
            download_debug
            ;;
        -br)
            download_release
            ;;
        -r)
            reinstall
            ;;
        -l)
            view_logs
            ;;
        -h)
            show_help
            ;;
        *)
            echo "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
    shift
done
