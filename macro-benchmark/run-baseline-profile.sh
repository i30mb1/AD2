#!/bin/bash

# Script to run baseline profile generation tests
# This script helps ensure all prerequisites are met before running tests

set -e

echo "🚀 AD2 Baseline Profile Generator"
echo "================================="

# Check if adb is available
if ! command -v adb &> /dev/null; then
    echo "❌ Error: adb not found. Please install Android SDK platform-tools."
    exit 1
fi

# Check for connected devices
DEVICES=$(adb devices | grep -w "device" | wc -l)
if [ "$DEVICES" -eq 0 ]; then
    echo "❌ Error: No Android devices connected."
    echo "   Please connect an Android device or start an emulator."
    echo "   Run 'adb devices' to check connected devices."
    exit 1
fi

echo "✅ Found $DEVICES connected device(s)"

# Build and install debug APK
echo "📱 Building and installing debug APK..."
./gradlew :app:installDebug

if [ $? -ne 0 ]; then
    echo "❌ Error: Failed to build or install debug APK"
    exit 1
fi

echo "✅ Debug APK installed successfully"

# Run the baseline profile generation test
echo "🔥 Running baseline profile generation..."
echo "   This may take 2-5 minutes depending on your device"

# Run the simple startup test by default (most reliable)
./gradlew :macro-benchmark:connectedBenchmarkAndroidTest \
    -Pandroid.testInstrumentationRunnerArguments.class=n7.ad2.macrobenchmark.StartupBaselineProfileGenerator

if [ $? -eq 0 ]; then
    echo "✅ Baseline profile generated successfully!"
    echo ""
    echo "📊 Results:"
    echo "   Baseline profile saved to: app/src/main/baseline-prof.txt"
    echo ""
    echo "🚀 Next steps:"
    echo "   1. Add the baseline-prof.txt file to your app module"
    echo "   2. Rebuild your app to benefit from improved startup performance"
    echo "   3. Expected improvement: 15-30% faster cold start"
    
    if [ -f "app/src/main/baseline-prof.txt" ]; then
        echo ""
        echo "📋 Generated profile preview:"
        head -10 app/src/main/baseline-prof.txt
        echo "   (... and more)"
    fi
else
    echo "❌ Baseline profile generation failed"
    echo "   Try running manually with: ./gradlew :macro-benchmark:connectedBenchmarkAndroidTest"
    exit 1
fi