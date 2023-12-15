# Kotlinova architecture demo

Playground / demo app for using Kotlinova architecture. It is simple app that loads data from https://dummyjson.com.
Features include:

* 100% Compose (single activity)
* Deep links to every screen
* Configuration change and process deaths handling
* Dark mode support
* Material You support
* Improved support for large screens (two-pane layout for user and post list)
* Offline support (data is saved to database and can be browsed offline)
* Pagination
* List of all reusable Compose components in-app (via Showkase)
* Unit tests for UI and all logic code
* Integration tests that test the whole app, optimized with R8
* Easy to use screenshot tests
* Macrobenchmark

See also other branches:

* `empty_kotlinova_project` = Empty template project with most of the kotlinova architecture set up
* `empty_project` = Barebones empty project with some essentials pre-created

# Readme for the application

## Updating versions

Process for updating all library versions::

1. Run `./gradlew dependencyUpdates :buildSrc:dependencyUpdates --no-configuration-cache > /dev/null
   && ./gradlew :app:updateLibsToml`.
   This will check for new versions of all libraries and automatically bump versions inside `libs.toml`.
2. Check the output of the above task and manually update any libraries that task said it couldn't update automatically.
3. Update Gradle version (see instructions in the [gradle wrapper's properties](gradle/wrapper/gradle-wrapper.properties))
2. Sync the project, build the app and run all tests to make sure everything still works.
3. Run `detektDebug` gradle task to find any new deprecated code
4. For any new deprecated code found by the search:
    * If the fix is trivial, fix it immediately as part of the update commit
    * Otherwise, make a tech debt ticket and add a @Suppress with the ticket number
      (and/or discuss with the team how to address it)

## Creating a new module

1. Copy module template from `config/templates` folder to root of the project and rename it to your module
2. If new module is an android library and if it needs android resources, add `buildFeatures { androidResources = true }`
   and namespace declaration to module's `build.gradle.kts`
3. Add module to `settings.gradle.kts`
4. Add module to app's `build.gradle.kts` as `implementation(projects.newModule)`)

## Hierarchy of the feature modules

Every feature should contain following modules:

* `feature-name`
    * `data` - data module with all non-UI logic (such as repositories).
      No other module (except for top level `app`) should depend on this.
    * `api` - interfaces and data models exposed to other modules.
      This module should generally contain no logic to speed up builds.
    * `ui` - Module containing feature's ui (Screens / Fragments / ViewModels)
    * `test` (optional) - Module containing test helpers for all other modules in this group

## Running integration tests

`./gradlew :app:connectedAndroidTest -PtestAppWithProguard`

# Creating screenshot tests

To create screenshot tests for your compose screen:

1. Make preview functions public
2. Add `showkase` plugin to the module of the screen you want to test
2. Add `@ShowkaseComposable(group = "Test")` annotation to the preview of the screen you want to test
3. run `recordPaparazziDebug` gradle task to record golden screenshot
   (double check that only previews for your screen were generated)
