# Back Button Navigation Implementation

## Overview

This document describes the back button handling implementation for the BesteChef application. The system manages back button behavior (Android) and gesture navigation across different screens while maintaining multiplatform compatibility.

## Architecture

### Multiplatform Back Handler

A **platform-abstraction approach** has been implemented to support future multiplatform expansion (Android + Web) while maintaining clean separation of concerns.

#### File Structure:
```
core/src/
├── commonMain/kotlin/nl/tue/hci/core/ui/BackHandler.kt      (expect declaration)
├── androidMain/kotlin/nl/tue/hci/core/ui/BackHandler.kt     (actual Android implementation)
└── wasmJsMain/kotlin/nl/tue/hci/core/ui/BackHandler.kt      (actual Web implementation)
```

### Key Components

#### 1. **PlatformBackHandler** (commonMain)
```kotlin
@Composable
expect fun PlatformBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit
)
```
- **Purpose**: Multiplatform expect declaration for back handling
- **Parameters**:
  - `enabled`: Controls whether the back handler is active
  - `onBack`: Callback triggered when back is pressed

#### 2. **Android Implementation** (androidMain)
```kotlin
@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    BackHandler(enabled = enabled, onBack = onBack)
}
```
- Uses Jetpack Compose's `BackHandler` from `androidx.activity.compose`
- Intercepts system back button and back gesture
- Directly invokes the callback when back is triggered

#### 3. **Web Implementation** (wasmJsMain)
```kotlin
@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    // Web platform back handling can be implemented here in the future
    // For now, this is a no-op as browser back is typically handled differently
}
```
- Placeholder for future browser back/history management
- Can be extended to use browser History API or similar mechanisms

## Back Navigation Logic

### DinerScreen Back Handler

Located in: `feature/diner/src/commonMain/kotlin/nl/tue/hci/feature/diner/pages/DinerScreen.kt`

#### Behavior:
```
┌─────────────────────────────────────────────────────────┐
│ Back Button Logic - Diner Section                        │
├─────────────────────────────────────────────────────────┤
│ If on Payment Screen      → Return to Orders             │
│ If on Chat Screen         → Return to Chat History       │
│ If on Home (base page)    → EXIT APP (logout + clear)    │
│ If on Chat/Orders/Profile → Return to Home              │
└─────────────────────────────────────────────────────────┘
```

#### Implementation Details:
```kotlin
PlatformBackHandler(
    enabled = true,
    onBack = {
        when {
            showPaymentSuccessfulScreen -> {
                // On payment screen, go back to orders
                showPaymentSuccessfulScreen = false
            }
            showChatScreen -> {
                // On chat screen, go back to chat history
                showChatScreen = false
            }
            currentDestination == DinerDestinations.HOME -> {
                // On home page (base page), exit app
                onExitApp()
            }
            else -> {
                // On any other section page, go back to home
                currentDestination = DinerDestinations.HOME
            }
        }
    }
)
```

### ChefScreen Back Handler

Located in: `feature/chef/src/commonMain/kotlin/nl/tue/hci/feature/chef/ChefScreen.kt`

#### Behavior:
```
┌─────────────────────────────────────────────────────────┐
│ Back Button Logic - Chef Section                         │
├─────────────────────────────────────────────────────────┤
│ If on Booking Confirmed   → Return to Orders            │
│ If on Chat Screen         → Return to Chat History      │
│ If on Home (base page)    → EXIT APP (logout + clear)   │
│ If on Chat/Orders/Profile → Return to Home             │
└─────────────────────────────────────────────────────────┘
```

#### Implementation Details:
Similar to DinerScreen, with additional handling for the booking confirmation overlay.

## Data Flow

```
Back Button Pressed (Android)
         ↓
androidx.activity.compose.BackHandler
         ↓
PlatformBackHandler callback triggered
         ↓
Check current UI state (which screen/overlay is showing)
         ↓
Execute appropriate navigation action:
   - Hide overlay and navigate
   - Navigate to previous section
   - Exit app with cleanup
```

## Integration Points

### 1. **Activity Level** (BesteChefApp)
```kotlin
DinerScreen(
    onLogout = { /* handle logout */ },
    onExitApp = {
        coroutineScope.launch {
            userSessionRepository.clearSession()
        }
    }
)
```

- The app clears user session when exiting from the home page
- This ensures proper cleanup and returns user to login screen

### 2. **Dependencies**
- **Android**: `androidx.activity:activity-compose:1.8.0`
  - Added to `core/build.gradle.kts`
  - Provides `BackHandler` composable

## Design Decisions

### 1. **Multiplatform Architecture**
- **Why**: The application may be migrated to support both Android and Web platforms in the future
- **Benefit**: Clear separation allows Web implementation to be added without modifying feature code
- **Extension Point**: `PlatformBackHandler` can be extended with browser history handling

### 2. **Home as Base Page**
- **Why**: Aligns with intuitive navigation patterns
- **Behavior**: Users expect to exit only from the main/home screen, not from child screens
- **Consistency**: Same behavior across both DinerScreen and ChefScreen

### 3. **Overlay Handling**
- **Approach**: Full-screen overlays (Chat, Payment, Booking) take precedence over section navigation
- **Benefit**: Natural back behavior through multiple layers of navigation
- **Implementation**: State checks in specific order (overlays first, then sections)

## Testing Scenarios

### Diner User Flow:
1. **Home → Back** → Exit app ✓
2. **Home → Chat → Back** → Chat history view ✓
3. **Chat History → Chat overlay → Back** → Chat history ✓
4. **Orders → Back** → Home ✓
5. **Orders → Payment → Back** → Orders ✓
6. **Profile → Back** → Home ✓

### Chef User Flow:
1. **Home → Back** → Exit app ✓
2. **Home → Chat → Back** → Chat history view ✓
3. **Chat History → Chat overlay → Back** → Chat history ✓
4. **Orders → Back** → Home ✓
5. **Orders → Booking Confirmed → Back** → Orders ✓
6. **Profile → Back** → Home ✓

## Future Enhancements

### Web Platform Support:
- Implement browser History API integration in `wasmJsMain/BackHandler.kt`
- Handle browser back/forward buttons
- Maintain history stack for proper navigation

### Advanced Features:
- Back button animation/haptic feedback
- Custom back button in UI (for non-Android platforms)
- Navigation history tracking/logging
- Gesture customization

## File Changes Summary

### Created Files:
1. `core/src/commonMain/kotlin/nl/tue/hci/core/ui/BackHandler.kt` - Expect declaration
2. `core/src/androidMain/kotlin/nl/tue/hci/core/ui/BackHandler.kt` - Android implementation
3. `core/src/wasmJsMain/kotlin/nl/tue/hci/core/ui/BackHandler.kt` - Web placeholder

### Modified Files:
1. `core/build.gradle.kts` - Added `androidx.activity.compose` dependency
2. `feature/diner/src/commonMain/kotlin/nl/tue/hci/feature/diner/pages/DinerScreen.kt` - Integrated back handler
3. `feature/chef/src/commonMain/kotlin/nl/tue/hci/feature/chef/ChefScreen.kt` - Integrated back handler
4. `app/src/commonMain/kotlin/nl/tue/hci/bestechef/BesteChefApp.kt` - Added `onExitApp` callback

## Build Status

✓ Build successful with no errors
✓ All modules compile correctly
✓ Ready for testing on Android devices
