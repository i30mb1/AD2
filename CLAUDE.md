# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

### Essential Commands

- Set `ROOT_MODULE` in `local.properties` to target a specific module (e.g., `ROOT_MODULE=feature:heroes`)
- **Build the project**: `.\gradlew build`
- **Run tests**: `.\gradlew test`
- **Run Android tests**: `.\gradlew connectedAndroidTest`
- **Lint check**: `.\gradlew lint` (Android lint configured with `abortOnError = true`)
- **Clean build**: `.\gradlew clean`
- **Build APK**: `.\gradlew assembleRelease` or `.\gradlew assembleDebug`

### Development Workflow

- **Run single test class**: `.\gradlew test --tests ClassName`
- **Build with profiling**: Uses custom `measure-build-plugin` for build time analysis
- **Detekt static analysis**: Available via `convention.detekt` plugin (currently commented out)

## Architecture Overview

This is a **multi-module Android application** following **Clean Architecture** principles.

### Module Structure

**Three-tier architecture:**

1. **`app/`** - Main application module that wires dependencies
2. **`feature/`** - Business features organized by domain (ui + business logic)
3. **`core/`** - Shared components and utilities

### Key Features

- **`feature/drawer`** - Main screen with draggable navigation
- **`feature/heroes`** - Hero information and listings
- **`feature/items`** - Game items and item details
- **`feature/games`** - Mini-games (mix, xo)
- **`feature/streams`** - Live Twitch.tv integration
- **`feature/news`** - Dota 2 news aggregation
- **`feature/tournaments`** - Tournament listings
- **`feature/settings`** - App configuration

### Core Infrastructure

- **`core/database`** - Room database with hero/item data
- **`core/repositories`** - Data access layer
- **`core/dagger`** - Dependency injection setup
- **`core/coroutines`** - Coroutines utilities and flow extensions
- **`core/navigator`** - Inter-feature navigation without direct dependencies
- **`core/span-parser`** - Custom HTML to Spannable conversion
- **`core/logger`** - Multi-target logging (app-logger, yandex)

### Technology Stack

- **Language**: Kotlin with Coroutines
- **DI**: Dagger 2 (using KSP2)
- **UI**: Jetpack Compose + traditional Views
- **Database**: Room with Paging 3
- **Networking**: Retrofit + OkHttp
- **Build**: Gradle with Kotlin DSL, AGP 9, Kotlin 2.1.20, and custom build-logic plugins

### Build Logic

Custom Gradle plugins in `build-logic/`:

- **Convention plugins** for Android/Kotlin setup
- **Version bumping** via `bump-version-plugin`
- **Build measurement** via `measure-build-plugin`
- **Custom detekt rules** including `UseLaunchSaveRule`

### Feature Module Pattern

Each feature follows Clean Architecture:

- **`ui/`** - Presentation layer (MVVM/MVI patterns)
- **`domain/api/`** - Public interfaces
- **`domain/impl/`** - Business logic (data layer is internal)
- **`domain/wiring/`** - Dependency injection wiring with Dagger2, only here dependency Dagger2 can be used
- **`demo/`** - Standalone demo apps for development specific feature

### Build Variants

- **debug** - Development with `.debug` suffix
- **release** - Production with ProGuard/R8
- **benchmark** - Performance testing

### Testing Structure

- **Unit tests**: JUnit 4
- **Android tests**: Espresso + AndroidX Test
- **Benchmarks**: Macro and micro benchmarks in dedicated modules
- **Custom rules**: Detekt custom rules with tests

### Data Flow

The app follows a repository pattern where features access data through repositories in `core/repositories`, which coordinate between network (Retrofit) and local storage (Room database).

### Основные правила

- Если ты не знаешь ответа просто напиши "Я не знаю". Не ври!
- За каждую решенную задачу я буду платить 5000$
