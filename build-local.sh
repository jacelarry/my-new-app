#!/bin/bash

# Build script for SMS Automation App
# This script mimics the CI/CD pipeline locally

set -e  # Exit on error

echo "🚀 Starting local build process..."
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if JDK is installed
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java is not installed or not in PATH${NC}"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
echo -e "${GREEN}✓ Java version: $JAVA_VERSION${NC}"
echo ""

# Make gradlew executable
chmod +x ./gradlew

# Clean build directory
echo -e "${YELLOW}🧹 Cleaning build directory...${NC}"
./gradlew clean --no-daemon
echo ""

# Build Debug APK
echo -e "${YELLOW}🔨 Building Debug APK...${NC}"
./gradlew :app:assembleDebug --no-daemon --stacktrace
echo ""

# Build Release APK (optional, comment out if you don't need it)
echo -e "${YELLOW}🔨 Building Release APK...${NC}"
./gradlew :app:assembleRelease --no-daemon --stacktrace
echo ""

# Run Unit Tests
echo -e "${YELLOW}🧪 Running Unit Tests...${NC}"
./gradlew :app:testDebugUnitTest --no-daemon --stacktrace
echo ""

# Check if APKs were created
DEBUG_APK="app/build/outputs/apk/debug/app-debug.apk"
RELEASE_APK="app/build/outputs/apk/release/app-release-unsigned.apk"

if [ -f "$DEBUG_APK" ]; then
    echo -e "${GREEN}✅ Debug APK created: $DEBUG_APK${NC}"
    ls -lh "$DEBUG_APK"
else
    echo -e "${RED}❌ Debug APK not found${NC}"
fi

echo ""

if [ -f "$RELEASE_APK" ]; then
    echo -e "${GREEN}✅ Release APK created: $RELEASE_APK${NC}"
    ls -lh "$RELEASE_APK"
else
    echo -e "${YELLOW}⚠️  Release APK not found (this is normal if unsigned)${NC}"
fi

echo ""
echo -e "${GREEN}🎉 Build completed successfully!${NC}"
echo ""
echo "📦 Outputs:"
echo "  - Debug APK: $DEBUG_APK"
echo "  - Release APK: $RELEASE_APK"
echo "  - Test Results: app/build/test-results/"
echo "  - Build Reports: app/build/reports/"

