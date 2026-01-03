# Back Button Navigation - Implementation Update

## Overview

Enhanced the back button handling system to fully address the requirements:

### Key Behaviors Implemented

#### 1. Home Page Back Button (DinerScreen & ChefScreen)
- **Behavior**: Directly exits the app (closes all activities)
- **Previous Issue**: Would clear session and show login page
- **Fix**: Uses `Activity.finishAffinity()` to cleanly exit without clearing session

#### 2. Secondary Pages Back Button
- **Behavior**: Returns to home page
- **Pages Affected**: Chat, Orders, Profile, etc.

#### 3. Overlay Back Button
- **Behavior**: Returns to the parent section
- **Overlays**: Chat details, Payment confirmation, Booking confirmation

#### 4. Login Screen Register Mode
- **Behavior**: Back button returns to sign-in (login) mode
- **Sign-in Mode**: Back button disabled (no action)
- **Profile Logout**: Only way to return to login screen after clearing session

## Technical Implementation

### New Components Created

#### 1. **AppExitHandler** (Multiplatform)
Files:
- `core/src/commonMain/kotlin/nl/tue/hci/core/ui/AppExitHandler.kt` (expect)
- `core/src/androidMain/kotlin/nl/tue/hci/core/ui/AppExitHandler.kt` (actual)
- `core/src/wasmJsMain/kotlin/nl/tue/hci/core/ui/AppExitHandler.kt` (actual)

**Function**:
```kotlin
@Composable
expect fun rememberAppExitHandler(): () -> Unit
```

**Android Implementation**:
```kotlin
@Composable
actual fun rememberAppExitHandler(): () -> Unit {
    val context = LocalContext.current
    return {
        (context as? Activity)?.finishAffinity()
    }
}
```

Uses `Activity.finishAffinity()` to:
- Close the current activity and all parent activities
- Cleanly exit the app without affecting session storage
- Not trigger logout or session clearing

**Web Implementation**:
- Placeholder for future browser exit handling

### Navigation Flow Diagrams

#### DinerScreen Back Navigation
```
HOME → Back → Exit App
├─ CHAT → Back → Home
│   └─ Chat Details → Back → Chat History
├─ ORDERS → Back → Home
│   └─ Payment Screen → Back → Orders
└─ PROFILE → Back → Home
    └─ Logout Button → Clear Session → Login Screen
```

#### ChefScreen Back Navigation
```
HOME → Back → Exit App
├─ CHAT → Back → Home
│   └─ Chat Details → Back → Chat History
├─ ORDERS → Back → Home
│   └─ Booking Confirmed → Back → Orders
└─ PROFILE → Back → Home
    └─ Logout Button → Clear Session → Login Screen
```

#### LoginScreen Back Navigation
```
Sign-in Mode → Back → No Action (back handler disabled)
└─ Continue → Register Mode → Back → Reset to Sign-in
```

## Modified Files

### Core Module
1. **core/build.gradle.kts**
   - Already had `androidx.activity.compose` dependency added

2. **core/src/androidMain/kotlin/nl/tue/hci/core/ui/AppExitHandler.kt** (new)
   - Android-specific app exit implementation

3. **core/src/wasmJsMain/kotlin/nl/tue/hci/core/ui/AppExitHandler.kt** (new)
   - Web placeholder for future implementation

### Feature: Diner
**feature/diner/src/commonMain/kotlin/nl/tue/hci/feature/diner/pages/DinerScreen.kt**

Changes:
- Added import: `import nl.tue.hci.core.ui.rememberAppExitHandler`
- Removed `onExitApp` parameter
- Added `val exitApp = rememberAppExitHandler()`
- Back handler now calls `exitApp()` instead of callback

### Feature: Chef
**feature/chef/src/commonMain/kotlin/nl/tue/hci/feature/chef/ChefScreen.kt**

Changes:
- Added import: `import nl.tue.hci.core.ui.rememberAppExitHandler`
- Removed `onExitApp` parameter
- Added `val exitApp = rememberAppExitHandler()`
- Back handler now calls `exitApp()` instead of callback

### Feature: Login
**feature/login/src/commonMain/kotlin/nl/tue/hci/login/LoginScreen.kt**

Changes:
- Added import: `import nl.tue.hci.core.ui.PlatformBackHandler`
- Added back handler: enabled only when in sign-up mode
- Calls `loginStateHolder.resetToSignIn()` on back

**feature/login/src/commonMain/kotlin/nl/tue/hci/login/LoginStateHolder.kt**

Changes:
- Added `resetToSignIn()` method to clear all input fields and reset state
- Returns to clean sign-in mode when back is pressed from register

### App
**app/src/commonMain/kotlin/nl/tue/hci/bestechef/BesteChefApp.kt**

Changes:
- Removed `onExitApp` callback from ChefScreen and DinerScreen invocations
- Callbacks now only pass `onLogout` (for profile logout button)

## Session Management Behavior

### Before (Issue)
```
Home Page Back → onExitApp() → clearSession() → userRole = null → Show Login Page
```

### After (Fixed)
```
Home Page Back → exitApp() [Activity.finishAffinity()] → App Exits Completely
                 Session preserved in storage

Profile Logout Button → clearSession() → userRole = null → Show Login Page
```

### Key Difference
- **Back Button**: Exits app without touching session (user stays logged in)
- **Logout Button**: Clears session and returns to login (user needs to sign in again)

## Build Status
✅ Build successful - no compilation errors
✅ All modules compile correctly
✅ Ready for testing on Android devices

## Testing Checklist

### DinerScreen
- [ ] Home page: Back exits app
- [ ] Chat page: Back returns to home
- [ ] Chat overlay: Back closes overlay, stays in chat
- [ ] Orders page: Back returns to home
- [ ] Payment overlay: Back closes overlay, stays in orders
- [ ] Profile page: Back returns to home
- [ ] Profile logout: Shows login screen

### ChefScreen
- [ ] Home page: Back exits app
- [ ] Chat page: Back returns to home
- [ ] Chat overlay: Back closes overlay, stays in chat
- [ ] Orders page: Back returns to home
- [ ] Booking confirmed overlay: Back closes overlay, stays in orders
- [ ] Profile page: Back returns to home
- [ ] Profile logout: Shows login screen

### LoginScreen
- [ ] Sign-in mode: Back does nothing
- [ ] Register mode: Back returns to sign-in (clears all fields)

## Future Enhancements

### Web Platform Support
- Implement browser History API in `wasmJsMain/AppExitHandler.kt`
- Handle browser back/forward buttons
- Maintain navigation history stack

### Advanced Features
- Back gesture animation/haptic feedback
- Custom back button UI (for platforms without system back)
- Navigation history logging
- Gesture customization

## Architecture Benefits

1. **Multiplatform Ready**: Different implementations per platform
2. **Session Preservation**: Exiting doesn't clear user data
3. **Clean Separation**: App exit vs. logout clearly distinguished
4. **Composable Pattern**: Uses Compose lifecycle management
5. **Type Safe**: No string-based navigation logic
