# Navigation playground

Playground for the creation of the StabbyStack navigation system (Stabby = dagger, Stack = Simple stack).

## Updating versions

Process for updating all library versions::

1. Run `./gradlew dependencyUpdates :buildSrc:dependencyUpdates --no-configuration-cache > /dev/null &&  ./gradlew :app:updateLibsToml`. This will check for new versions of all libraries 
   and automatically bump versions inside `libs.toml`.
2. Check the output of the above task and manually update any libraries that task said it couldn't update automatically.
2. Sync the project, build the app and run all tests to make sure everything still works.
3. Open Code -> Analyze code dialog, and scan the whole project with the "Deprecated code" profile:
   ![analyze-deprecated.png](docs/analyze-deprecated.png)
4. For any new deprecated code found by the search:
    * If the fix is trivial, fix it immediately as part of the update commit
    * Otherwise, make a tech debt ticket (and/or discuss with the team how to address it) 

## Creating a new module

1. Copy module template from `config/templates` folder to root of the project and rename it to your module
2. If target module is android library, change its namespace in module's `build.gradle.kts`
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
