# About Dota 2

All you want to know about dota in one app

[![CC0](readme-files/google_play.png)](https://play.google.com/store/apps/details?id=n7.ad2)

## Tech-stack

<img src="readme-files/app.gif" width="300" align="right" hspace="20">

* Tech-stack
    * [100% Kotlin](https://kotlinlang.org/) + [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)
    * [Retrofit](https://square.github.io/retrofit/)
    * [Jetpack](https://developer.android.com/jetpack)
        * [Room](https://developer.android.com/topic/libraries/architecture/room) - store offline cache
        * [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - store and manage UI-related data in a
          lifecycle
        * [Lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle) - perform action when lifecycle state changes
        * [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - notify views about database change
        * [Data Binding](https://developer.android.com/topic/libraries/data-binding) - library that allows you to bind UI components in your
          layouts to data sources in your app using a declarative format rather than programmatically.
        * [WorkManager](https://developer.android.com/reference/androidx/work/WorkManager) - the WorkManager API makes it easy to schedule
          deferrable, asynchronous tasks that are expected to run even if the app exits or device restarts.
        * [Paging](https://developer.android.com/topic/libraries/architecture/paging/) - the Paging Library helps you load and display small
          chunks of data at a time. Loading partial data on demand reduces usage of network bandwidth and system resources.
    * [InstantApp](https://developer.android.com/topic/google-play-instant) - Native Android apps, without the installation
    * [Firebase](https://firebase.google.com/) - Firebase is a comprehensive mobile development platform. They give you the tools to develop
      high-quality apps, grow your user base, and earn more money.
    * [Jsoup](https://jsoup.org/) - library for working with real-world HTML. It provides a very convenient API for extracting and
      manipulating data, using the best of DOM, CSS, and jquery-like methods.
* CI
    * [GitHub Actions](https://github.com/features/actions)
    * Automatic PR verification including tests, linters and 3rd online tools
* Testing
    * [Unit Tests](https://en.wikipedia.org/wiki/Unit_testing) ([JUnit 4](https://junit.org/junit4/)
    * [UT Tests](https://en.wikipedia.org/wiki/Graphical_user_interface_testing) ([Espresso](https://developer.android.com/training/testing/espresso))
    * [Truth](https://truth.dev/) - assertion framework
* Gradle
    * [Gradle Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
    * [Versions catalog](https://docs.gradle.org/7.0-milestone-1/userguide/platforms.html)
    * Custom tasks

## Architecture

### Module types and module dependencies

This diagram presents dependencies between project modules (Gradle sub-projects).

![module_dependencies](https://github.com/igorwojda/android-showcase/blob/main/misc/image/module_dependencies.png?raw=true)

We have three kinds of modules in the application:

- `app` module - this is the main module. It contains code that wires multiple modules together (dependency injection setup) and fundamental
  application configuration.
- `core/*` modules that some of the features could depend on.
- `feature/*` modules that containing all code related to a given feature.

### Data flow

![app_data_flow](readme-files/data_flow.png)

## Getting started with Android Studio

1. `Android Studio` -> `File` -> `New` -> `From Version control` -> `Git`
2. Enter `https://github.com/i30mb1/AD2.git` into URL field an press `Clone` button