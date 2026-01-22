# BesteChef Build Manual

## Project Structure

The BesteChef app uses Kotlin Multiplatform to share code across multiple platforms:

```
BesteChef/
├── app/                      # Main application module
│   └── src/
│       ├── commonMain/       # Shared UI code (Compose)
│       ├── androidMain/      # Android-specific code
│       ├── wasmJsMain/       # Web browser code
│       └── desktopMain/      # macOS native desktop code
│
├── core/                     # Core functionality module
│   └── src/
│       ├── commonMain/       # Shared core logic
│       ├── androidMain/      # Android DataStore
│       ├── wasmJsMain/       # Browser localStorage
│       └── desktopMain/      # File-based storage + ImageLoader
│
└── feature/                  # Feature modules
    ├── login/
    ├── chef/
    └── diner/
        └── src/
            ├── commonMain/   # Shared feature code
            ├── androidMain/  # Android notifications
            ├── wasmJsMain/   # Web features
            └── desktopMain/  # Desktop notifications
```

---

## How to Run Each Version

### 1. Native macOS App (Recommended)

Build and run the native macOS application:

```bash
# Build the app
./gradlew :app:createDistributable

# Run the app
open app/build/compose/binaries/main/app/BesteChef.app
```

**Features:**
- Window size: 430×932 pixels (iPhone 15 Pro Max dimensions)
- Resizable with maintained aspect ratio (430:932)
- Embedded JVM (no Java installation required)
- File-based storage in `~/.bestechef/`

**Create installer packages:**
```bash
./gradlew :app:packageDistributionForCurrentOS
```
Output: DMG and PKG files in `app/build/compose/binaries/main/`

---

### 2. Web Browser Version

Build and run in any modern web browser:

**Production build:**
```bash
./gradlew wasmJsBrowserProductionWebpack
open build/js/packages/BesteChef-app-wasm-js/kotlin/index.html
```

**Development build (faster compilation):**
```bash
./gradlew wasmJsBrowserDevelopmentWebpack
open build/js/packages/BesteChef-app-wasm-js-test/kotlin/index.html
```

**Features:**
- Runs in Chrome, Safari, Firefox, Edge
- Uses browser localStorage for data
- ~7.9 MB bundle size

---

### 3. Android App

Build Android APK:

**Debug build:**
```bash
./gradlew :app:assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

**Release build:**
```bash
./gradlew :app:assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`

**Features:**
- Native Android app
- Uses Android DataStore for persistence
- Supports Android 7.0+ (API 24+)

---

## Build All Platforms

To build everything at once:

```bash
./gradlew build
```

This creates:
- Android debug and release APKs
- Web WASM bundle
- Desktop native app
- All intermediate artifacts

---

## System Requirements

| Platform | Requirements |
|----------|-------------|
| **macOS Native** | macOS 10.12+, ARM64/x86_64, 4GB RAM |
| **Web Browser** | Any modern browser with WebAssembly support |
| **Android** | Android 7.0+ (API 24+) |
| **Development** | JDK 11+, Gradle 8.13, Kotlin 2.3.0 |

---

## Quick Reference

| Task | Command |
|------|---------|
| Run macOS app | `./gradlew :app:createDistributable && open app/build/compose/binaries/main/app/BesteChef.app` |
| Run in browser | `./gradlew wasmJsBrowserProductionWebpack && open build/js/packages/BesteChef-app-wasm-js/kotlin/index.html` |
| Build Android APK | `./gradlew :app:assembleDebug` |
| Build all platforms | `./gradlew build` |
| Clean build | `./gradlew clean build` |
