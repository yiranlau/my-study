#!/bin/bash
set -e

# Android CI Helper Script
# Usage:
#   -b          Build debug APK via GitHub Actions and download
#   -d          Download latest debug APK from CI
#   -r          Reinstall debug APK on connected device
#   -br         Download latest release APK from GitHub Releases
#   -l          View logcat for the debug package
#   -h          Show help

PACKAGE_NAME="com.study.app"
APP_NAME="Study"

function build_and_download() {
    echo "=== Building debug APK via GitHub Actions ==="

    # Trigger workflow and get run ID
    echo "Triggering Android CI workflow..."
    RUN_URL=$(gh workflow run android-ci.yml)
    # Extract run ID from URL like "https://github.com/owner/repo/actions/runs/12345678"
    RUN_ID=$(echo "$RUN_URL" | grep -oE '[0-9]+$')

    if [ -z "$RUN_ID" ] || [ "$RUN_ID" = "null" ]; then
        echo "Failed to trigger workflow"
        exit 1
    fi

    echo "Workflow triggered: $RUN_ID"

    # Wait for workflow to complete
    echo "Waiting for build to complete..."
    gh run watch "$RUN_ID"

    # Check if successful
    STATUS=$(gh run view "$RUN_ID" --json status --jq '.status')
    CONCLUSION=$(gh run view "$RUN_ID" --json conclusion --jq '.conclusion')

    if [ "$CONCLUSION" != "success" ]; then
        echo "Build failed with conclusion: $CONCLUSION"
        exit 1
    fi

    # Download artifacts
    echo "Downloading build artifacts..."
    gh run download "$RUN_ID" -n build-artifacts --dir ./downloads

    APK_PATH="./downloads/app/build/outputs/apk/debug/app-debug.apk"
    if [ -f "$APK_PATH" ]; then
        echo "APK downloaded: $APK_PATH"
    else
        echo "APK not found in artifacts"
        ls -la ./downloads/
        exit 1
    fi
}

function download_debug() {
    echo "=== Downloading latest debug APK from CI ==="

    # Find latest successful CI run
    RUN_ID=$(gh run list --workflow android-ci.yml --status success --limit 1 --json databaseId --jq '.[0].databaseId')

    if [ -z "$RUN_ID" ] || [ "$RUN_ID" = "null" ]; then
        echo "No successful CI run found"
        exit 1
    fi

    echo "Found successful run: $RUN_ID"

    # Download artifacts
    echo "Downloading build artifacts..."
    gh run download "$RUN_ID" -n build-artifacts --dir ./downloads

    APK_PATH="./downloads/app/build/outputs/apk/debug/app-debug.apk"
    if [ -f "$APK_PATH" ]; then
        echo "APK downloaded: $APK_PATH"
    else
        echo "APK not found in artifacts"
        ls -la ./downloads/
        exit 1
    fi
}

function download_release() {
    echo "=== Downloading latest release APK from GitHub Releases ==="

    # Find latest release
    RELEASE=$(gh release view --json tagName,uploadUrl --jq '{
        tag: .tagName,
        url: .uploadUrl
    }')

    if [ -z "$RELEASE" ]; then
        echo "No release found"
        exit 1
    fi

    TAG=$(echo "$RELEASE" | jq -r '.tag')
    echo "Found release: $TAG"

    # Download release assets
    mkdir -p ./downloads/release
    gh release download "$TAG" --dir ./downloads/release --pattern "*.apk"

    APK_PATH=$(find ./downloads/release -name "*.apk" | head -1)
    if [ -n "$APK_PATH" ]; then
        echo "Release APK downloaded: $APK_PATH"
    else
        echo "No APK found in release"
        ls -la ./downloads/release/
        exit 1
    fi
}

function reinstall() {
    echo "=== Reinstalling debug APK on device ==="

    APK_PATH="./downloads/app/build/outputs/apk/debug/app-debug.apk"

    if [ ! -f "$APK_PATH" ]; then
        echo "APK not found at $APK_PATH"
        echo "Run with -b or -d first"
        exit 1
    fi

    # Uninstall existing package
    echo "Uninstalling existing package..."
    adb uninstall "$PACKAGE_NAME" 2>/dev/null || true

    # Install new APK
    echo "Installing new APK..."
    adb install -r "$APK_PATH"

    # Launch app
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
    echo "Usage: $0 [-b] [-d] [-r] [-br] [-l] [-h]"
    echo ""
    echo "Options:"
    echo "  -b    Build debug APK via GitHub Actions and download"
    echo "  -d    Download latest debug APK from CI"
    echo "  -r    Reinstall debug APK on connected device"
    echo "  -br   Download latest release APK from GitHub Releases"
    echo "  -l    View logcat for the debug package"
    echo "  -h    Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 -b          # Build and download APK"
    echo "  $0 -d          # Download latest debug APK"
    echo "  $0 -r          # Reinstall APK on device"
    echo "  $0 -br         # Download latest release APK"
    echo "  $0 -b -r       # Build, download and reinstall"
}

# Parse arguments
while getopts "bdr lh" opt; do
    case $opt in
        b)
            BUILD=true
            ;;
        d)
            DOWNLOAD_DEBUG=true
            ;;
        r)
            REINSTALL=true
            ;;
        l)
            LOGS=true
            ;;
        h)
            show_help
            exit 0
            ;;
        \?)
            show_help
            exit 1
            ;;
    esac
done

# Handle -br as a special case (can't use getopts for it)
if [[ "$@" == *"-br"* ]]; then
    DOWNLOAD_RELEASE=true
fi

# Execute actions
if [ "$BUILD" = true ]; then
    build_and_download
fi

if [ "$DOWNLOAD_DEBUG" = true ]; then
    download_debug
fi

if [ "$DOWNLOAD_RELEASE" = true ]; then
    download_release
fi

if [ "$REINSTALL" = true ]; then
    reinstall
fi

if [ "$LOGS" = true ]; then
    view_logs
fi

# No options provided
if [ -z "$BUILD" ] && [ -z "$DOWNLOAD_DEBUG" ] && [ -z "$DOWNLOAD_RELEASE" ] && [ -z "$REINSTALL" ] && [ -z "$LOGS" ]; then
    show_help
fi
