# Guide for Newcomers - MVVM Compose Android App

## Introduction

Welcome to the MVVM Compose project! This is an Android application built with the modern MVVM (Model-View-ViewModel) architecture, using Jetpack Compose for UI and the latest technologies from Google. This document will help you understand the project structure, key components, and how to work effectively with the codebase.

## Technical Requirements

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 (minimum)
- Kotlin 2.0.0 or newer
- Gradle 8.0 or newer

## Project Structure

The project is organized into modules and packages to facilitate development, testing, and maintenance:

```
app/
  ├── src/main/
  │   ├── java/com/pixelzlab/app/
  │   │   ├── core/               # Core components used throughout the app
  │   │   │   ├── base/           # Base classes (BaseViewModel, BaseScreen, etc.)
  │   │   │   ├── navigation/     # Navigation definitions
  │   │   │   ├── network/        # Network handling and error management
  │   │   │   └── ui/             # Shared UI components
  │   │   │       └── state/      # UI state management
  │   │   ├── data/               # Data layer
  │   │   │   ├── local/          # Local data (DataStore, Room)
  │   │   │   └── remote/         # Remote data from API
  │   │   │       ├── api/        # API definitions
  │   │   │       └── repository/ # Repositories
  │   │   ├── designsystem/       # Design system
  │   │   │   ├── component/      # Reusable UI components
  │   │   │   └── theme/          # Theme definitions (colors, typography, etc.)
  │   │   ├── di/                 # Dependency Injection (Hilt)
  │   │   ├── feature/            # App features
  │   │   │   ├── detail/         # Detail screen
  │   │   │   ├── home/           # Home screen
  │   │   │   └── splash/         # Splash screen
  │   │   ├── model/              # Models
  │   │   │   ├── base/           # Base models
  │   │   │   └── entity/         # Entities
  │   │   ├── startup/            # App initialization with App Startup
  │   │   └── utils/              # Utilities
  │   │       └── extension/      # Extension functions
  │   └── res/                    # Resources (layouts, drawables, etc.)
  └── build.gradle.kts            # Gradle configuration for app module
```

## Application Architecture

The project follows the MVVM (Model-View-ViewModel) architecture with Clean Architecture principles:

1. **UI Layer (View)**: Jetpack Compose UI
   - Screens and UI components
   - Uses UiState for UI state management

2. **ViewModel Layer**: Manages business logic and state
   - Connects UI with data layer
   - Handles user events
   - Manages UI state through StateFlow

3. **Data Layer**: Manages data
   - Repository: Mediator between ViewModel and data sources
   - Remote Data Source: API services (Retrofit)
   - Local Data Source: DataStore, Room Database

## Key Technologies

- **Jetpack Compose**: Modern UI framework based on Kotlin
- **Coroutines & Flow**: Asynchronous programming
- **Hilt**: Dependency Injection
- **Retrofit & OkHttp**: Network requests
- **Moshi**: JSON parsing
- **DataStore**: Local data storage
- **App Startup**: Optimized app initialization
- **Material 3**: Design system
- **Coil**: Image loading and display

## Getting Started Guide

### 1. Clone and Open the Project

```bash
git clone [repository-url]
cd mvvm-compose
```

Open the project in Android Studio.

### 2. Understanding the Navigation Flow

The application uses Jetpack Navigation Compose for navigating between screens. The `AppNavigation.kt` file in the `core/navigation` package defines the entire navigation of the app.

### 3. Adding a New Feature

To add a new feature:

1. Create a new package in `feature/`
2. Create the following files:
   - `FeatureScreen.kt`: UI with Compose
   - `FeatureViewModel.kt`: ViewModel for the feature
   - Feature-specific UI components

3. Add a new destination in `AppDestination.kt`
4. Update the navigation in `AppNavigation.kt`

### 4. Managing UI State

The project uses the UiState pattern to manage UI state consistently:

```kotlin
// Using UiState in ViewModel
val uiState: StateFlow<UiState<YourData>> = _uiState

// In Composable
val state by viewModel.uiState.collectAsStateWithLifecycle()
when (state) {
    is UiState.Loading -> // Show loading
    is UiState.Success -> // Show data
    is UiState.Error -> // Show error
}
```

### 5. Working with APIs

1. Define the endpoint in `ApiService.kt`
2. Add a repository method in `AppRepository.kt`
3. Use the repository in the ViewModel through dependency injection

```kotlin
@Inject
lateinit var repository: AppRepository

fun fetchData() {
    viewModelScope.launch {
        repository.getData()
            .onStart { _uiState.value = UiState.Loading }
            .catch { e -> _uiState.value = UiState.Error(e) }
            .collect { data -> _uiState.value = UiState.Success(data) }
    }
}
```

## Rules and Conventions

### Coding Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use `./gradlew ktlintCheck` to check code style
- Use `./gradlew ktlintFormat` to automatically format code

### Naming Conventions

- **Classes**: PascalCase (e.g., `HomeViewModel`)
- **Functions**: camelCase (e.g., `fetchUserData()`)
- **Variables**: camelCase (e.g., `userName`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`)
- **Composable Functions**: PascalCase (e.g., `HomeScreen()`)

### Composable Functions

- Main Composable functions should be prefixed with the feature name (e.g., `HomeScreen`)
- Secondary Composable functions should have private scope if only used within the file
- Use the `modifier` parameter as the first parameter after default value parameters

```kotlin
@Composable
fun MyComponent(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    // Implementation
}
```

### Dependency Injection

- Use Hilt for DI
- Define modules in the `di/` package
- Use `@Inject` constructor when possible
- Use `@Provides` for cases where direct injection is not possible

## Performance and Optimization

- Use `remember` and `derivedStateOf` to avoid unnecessary recalculations
- Use `LaunchedEffect` for side effects
- Avoid allocation in Composable functions
- Use `key` for list items to optimize recomposition

## Debugging and Logging

- Use Timber for logging
- Use Compose Preview for rapid UI development
- Use Layout Inspector to debug UI
- Use StrictMode to detect performance issues

## Learning Resources

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Hilt Documentation](https://dagger.dev/hilt/)
- [Material 3 Design](https://m3.material.io/)
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)

## Support Contact

If you have any questions or encounter issues, please contact:

- Team Lead: [email@example.com]
- Tech Support: [support@example.com]

---

We hope you have a great experience working with the MVVM Compose project! 