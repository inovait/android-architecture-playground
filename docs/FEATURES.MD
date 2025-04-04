# 100% Compose (single activity)

Only activity/fragment is [MainActivity](../app/src/main/kotlin/si/inova/androidarchitectureplayground/MainActivity.kt) and the
whole app only creates one view - ComposeView.

Navigation is handled by the [kotlinova-navigation](https://github.com/inovait/kotlinova/blob/master/navigation) library.

# Deep links to every screen

Various deep link handlers across the app, demonstrating kotlinova navigation deep link handlers

For example [HomeDeepLinks](../home/ui/src/main/kotlin/si/inova/androidarchitectureplayground/home/HomeDeepLinks.kt)

# Configuration change and process deaths handling

kotlinova-navigation handles configuration changes and process deaths out of the box and it includes [useful utilities
to save state inside ViewModel](https://github.com/inovait/kotlinova/tree/master/navigation#saveable-scoped-service)
(such as `by saved` extension)

# Dark mode and Material You support

Mostly handled by Google's material libraries, but enabled in
the [Theme](../common-compose/src/main/kotlin/si/inova/androidarchitectureplayground/ui/theme/Theme.kt) file.

# Improved support for large screens (two-pane layout for user and post list)

* [MasterDetailScreen](../common-navigation/src/main/kotlin/si/inova/androidarchitectureplayground/navigation/base/MasterDetailScreen.kt)
  allows very easy creation of the [master-detail](https://m3.material.io/foundations/layout/canonical-layouts/list-detail)
  pattern
* [HomeScreen](../home/ui/src/main/kotlin/si/inova/androidarchitectureplayground/home/HomeScreen.kt)
  includes ["navigation rail on large screens, bottom bar on smaller screens"](https://m3.material.io/components/navigation-rail/overview),
  as recommended by Google.

# Offline support

* Both post and user data use SQLDelight to store data
* Every module handles its own tables, all of which get merged into one database in the `app` module
* CI runs `verifyDebugDatabaseMigration` to ensure all database migration files are valid at all times

# Pagination

[PaginatedDataStream](../common/src/main/kotlin/si/inova/androidarchitectureplayground/common/pagination/PaginatedDataStream.kt)
and its implementations allow easy implementation of paginated data sources

# List of all reusable Compose components in-app (via Showkase)

All reusable components have `@ShowkaseComposable` annotation and there is a "Components" button in the settings app.

# Unit tests for UI and all logic code

* All classes in `:data` modules and ViewModels have unit tests that verifies their correctness
* All screens have instrumented tests that verify user interaction
* All Retrofit interfaces are tested with the MockWebServer to ensure they actually load the data and decode the JSON properly

* All unit tests use JUnit5 + kotest assertions, which are auto included as a dependency in every module for convenience
* kotest assertion's rules config is set up more pragmatically to allow printing larger diffs
* Tests can include [unmock](https://github.com/bjoernQ/unmock-plugin), which allows use of utility Android classes (such as
  `Uri`) inside JVM unit tests

# Integration tests that test the whole app

`androidTest` tests in the `app` module:

* Initialize the whole app with proper DI instead of just instantiating screens/classes individually
* Instantiate everything as it would in the production app, except for network requests, which are replaced with the
  MockWebServer,
  to make tests independent of the server
* Navigate to every screen to ensure navigation does not break
* Are run after R8 minifying, to catch any issues that might arise from it

# Easy to use screenshot tests

`app-screenshot-test` module:

* Includes screenshot tests with Paparazzi
* To add a screen to the test, you only need to annotate its preview with the `@ShowkaseComposable(group = "Test")` annotation
* Tests only run on CI. This prevents differences from different developer machines from breaking tests.
* New images are also created on CI - developer must delete old images to "update" them
* Tests run in different splits, allowing them to run in parallel
* They test both light/day and small/large device with a single preview
* All images are stored in Git LFS to reduce repository size

# Macrobenchmark

`app-benchmark` module:

* Includes sample startup benchmark
* Includes sample scrolling benchmark
* Include sample auto-login setup in
  the [MainViewModel](../app/src/main/kotlin/si/inova/androidarchitectureplayground/MainViewModel.kt).
* [benchmark.yml](../.github/workflows/benchmark.yaml) is a sample CI setup to run those benchmark periodically on the Firebase
  Test Lab to
  detect any regressions in app performance over time

# Kotlin Inject

Project includes full setup of
the [kotlin-inject](https://github.com/evant/kotlin-inject) + [kotlin-inject-anvil](https://github.com/amzn/kotlin-inject-anvil)
for the Android app. This allows easy dependency injection with minimal boilerplate and it also future proofs the project
for potential future Kotlin Multiplatform integration.

# Modularisation

To achieve maximum scalability, project uses `api`/`data`/`ui`/`test` module groups for every features.

# Shared build logic

Most of the build logic consists of reusable convention plugins in the shared `buildSrc`. Individual app modules can opt-in into
various behaviors by just including these plugins inside the `plugins {}` block

# Pure kotlin modules + optimized Android modules

To improve scalability of this setup (especially Gradle configuration time) with many modules:

* As many modules as possible are only Kotlin modules (not Android) - these are much less complex and take less time to configure
* Android modules are as lean as possible by default - no resources etc. Every feature must be enabled explicitly
* Android modules have disabled instrumented tests by default - they must be explicitly enabled in the module's build file.

# Native Splash

App uses Android's native splash screen to load. Splash screen's duration is extended
until [MainViewModel](../app/src/main/kotlin/si/inova/androidarchitectureplayground/MainViewModel.kt) loads all needed async
data (artificial delay is introduced to demonstrate this functionality).

# Detekt

App includes [Detekt](https://github.com/detekt/detekt/) implementation with:

* Pragmatic rule configuration
* Sample CI scripts
* Automatic pre-commit hook configuration
* [detekt-compiler-rules](https://github.com/BraisGabin/detekt-compiler-rules) plugin brings all compiler warnings into detekt,
  allowing detection on CI
* Extra [compose-rules](https://mrmans0n.github.io/compose-rules/) for less Compose code smells
* Pre-commit hook generates `merged.sarif` file in the root of the project. This file can be opened by
  the [Qodana IDE plugin](https://plugins.jetbrains.com/plugin/16938-qodana) to easily find out why commit failed.
* [verifyPr.yaml](../.github/workflows/verifyPr.yaml) sample CI script will post all failures as detailed comments on the PR,
  allowing easy diagnosing on why PR build failed

# Lint

* App includes sample configuration for Android Lint + CI run on every PR
* [verifyPr.yaml](../.github/workflows/verifyPr.yaml) sample CI script will post all failures as detailed comments on the PR,
  allowing easy diagnosing on why PR build failed
  App includes CI configuration

# Module Graph assert

App uses [module-graph-assert](https://github.com/jraska/modules-graph-assert) plugin to enforce optimal modularisation in
the [app-module.gradle.kts](../buildSrc/src/main/kotlin/app-module.gradle.kts):

* It limits max module graph height to 6
* It prevents anyone from depending on the `:data` and `:ui` modules

# Dependency check

App includes [dependency-analysis-gradle](https://github.com/autonomousapps/dependency-analysis-gradle-plugin) plugin to ensure
no dependencies are being used unnecessarily (+ set up in CI scripts)

# Prevention of too large top and bottom modules

Experiment: [DetectTooManyKotlinFilesTask](../buildSrc/src/main/kotlin/tasks/DetectTooManyKotlinFilesTask.kt) prevents from
`app` module and `common` modules from growing too large, impacting build speeds.

# Compose stability checks

[ComposeGuard](https://github.com/j-roskopf/ComposeGuard) ensures all commited Composables are always stable to prevent
performance issues with Compose. App includes optimized configuration of that plugin with proper support for incremental builds
and Gradle build cache.

# Extra logging for crash debugging

Experiment: All user actions on the ViewModel are first logged to the ActionLogger. This would then be sent to the Firebase in
case of a crash to aid with
debugging. [Custom detekt rule](../detekt/src/main/kotlin/si/inova/androidarchitectureplayground/detekt/UseActionLoggerInViewModels.kt)
enforces its use.

You can add a following live template to the Android Studio to make creating these very easy:

```xml

<template name="al" value="actionLogger.logAction { &quot;$content$&quot; }"
    description="Create action logger entry with automatic parameter insertion" toReformat="false" toShortenFQNames="true">
    <variable name="content"
        expression="groovyScript(&quot;def params = _2.collect { it + ' = $' + it}.join(', '); return _3 + '.' + _1 + '(' + params + ')';&quot;, kotlinFunctionName(), functionParameters(), kotlinClassName()) "
        defaultValue="" alwaysStopAt="true" />
    <context>
        <option name="KOTLIN_EXPRESSION" value="true" />
        <option name="KOTLIN_STATEMENT" value="true" />
    </context>
</template>
```

Similarly, `MainActivity.LogCurrentScreen` contains all preparation for logging currently displayed screen.

# Strict Mode

[MyApplication](../app/src/main/kotlin/si/inova/androidarchitectureplayground/MyApplication.kt) includes sample strict mode
enabling that will crash debug builds if developer does something undesirable (such as read disk on the main thread). System
also includes an easy way to exclude false positives and errors that we cannot fix (for example, from Android System).

# What The Stack

[WhatTheStack](https://github.com/fvito/WhatTheStack) library will display a stack trace to the user on debug/staging builds.
This allows easier crash reporting while developing.

# Coroutine debug

[MyApplication](../app/src/main/kotlin/si/inova/androidarchitectureplayground/MyApplication.kt) enables better coroutine
stack traces on debug builds. They slow down the app, but they make debugging coroutine issues easier.

# Configuration-cache friendly versioning and BuildConfig

[app-module.gradle.kts](../buildSrc/src/main/kotlin/app-module.gradle.kts) sets up configuration-cache friendly determination of
the
version name, version code and BuildConfig variables, using git hashes by default as an example. This allows Gradle to reuse
configuration cache even when those variables change.

# CrashOnDebugException

Reporting [CrashOnDebugException](../common/src/main/kotlin/si/inova/androidarchitectureplayground/common/exceptions/CrashOnDebugException.kt)
will crash the app on debug/staging builds, but only report it to Firebase on production. This allows quick detection of some
behavior that should not happen, without crashing users in production.

# Automatic reload after user idle

[AwayDetectorFlow](../common/src/main/kotlin/si/inova/androidarchitectureplayground/common/flow/AwayDetectorFlow.kt) allows
easy implementation of automatic reloads when user re-opens the app after a longer period of being in the background.

# Multi-preview

Included [FullScreenPreviews](../common-compose/src/main/kotlin/si/inova/androidarchitectureplayground/ui/debugging/FullScreenPreviews.kt)
allows easily creating Compose previews that render in many different sizes/styles, to easily verify that your screen works on
different devices.

# Coil preview substitution

[PreviewTheme](../common-compose/src/main/kotlin/si/inova/androidarchitectureplayground/ui/debugging/PreviewTheme.kt) includes a
fake Coil loader that will display different color rectangles instead of images, allowing previewing and screenshot testing
of Composables that would ordinarily have to load images from the internet.

# Easy Retrofit service creation

`ServiceFactory` system makes it very easy to create modularized, small Retrofit services.
See `si.inova.androidarchitectureplayground.post.PostsDataComponent.providePostsService()` for an example.

# Easy dependency updating

Included [version-catalog-udpate-plugin](https://github.com/littlerobots/version-catalog-update-plugin) allows easy updating
of all dependencies in the project.

# Pre-commit hook that prevents pushing from some branches

When using a remote without branch protection, one can quickly accidentally push code into main branch.
We have a simple [pre-push](../config/hooks/pre-push) hook to prevent this
(hook is disabled by default, enable it by removing `exit 0` at the start). 
