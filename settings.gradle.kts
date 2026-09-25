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
      mavenLocal()
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

plugins {
   // We cannot use version catalogs here. So this is the only one place, where we must manually declare a dependency
   id("io.github.eduardbosch.module.graph.assertion.settings") version "2.10.1"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "AndroidArchitecturePlayground"

include(":app")
include(":app-benchmark")
include(":app-merged-instrumented-tests")
include(":app-screenshot-tests")
include(":common")
include(":common-android")
include(":common-compose")
include(":common-navigation")
include(":common-paging")
include(":common-retrofit")
include(":common-retrofit:android")
include(":detekt")
include(":home:api")
include(":home:ui")
include(":navigation-impl")
include(":login:api")
include(":login:data")
include(":login:ui")
include(":user:api")
include(":user:data")
include(":user:ui")
include(":post:api")
include(":post:data")
include(":post:ui")
include(":shared-resources")
