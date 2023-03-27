# Navigation playground

Playground for the creation of the StabbyStack navigation system (Stabby = dagger, Stack = Simple stack).

## Updating versions

To update all project's libraries, simply run the following command:

`./gradlew dependencyUpdates -Porg.gradle.unsafe.configuration-cache=false > /dev/null && ./gradlew :app:updateLibsToml`

This will check for new versions of all libraries and automatically bump versions inside `libs.toml`. Afterwards, check all
libraries that were updated and ensure that nothing broke.
