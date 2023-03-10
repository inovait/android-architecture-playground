// https://youtrack.jetbrains.com/issue/KTIJ-19369
// AGP 7.4.0 has a bug where it marks most things as incubating
@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

include(":anvil")


pluginManagement {
   repositories {
      google()
      mavenCentral()
      gradlePluginPortal()
   }
}

dependencyResolutionManagement {
   repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

   repositories {
      google()
      mavenCentral()
      maven("https://jitpack.io")
   }

   versionCatalogs {
      create("libs") {
         from(files("config/libs.toml"))
      }
   }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "AndroidArchitecturePlayground"

include(":app")
include(":common:android")
include(":common:android:test")
include(":common:pure")
include(":common:pure:test")
include(":libmodule")
include(":login")
include(":navigation-common")
include(":screen-c")
include(":ui-common")
include(":pure-kotlin-module")
include(":products:ui")
include(":products:data")
include(":network-common")
include(":network-common:android")
