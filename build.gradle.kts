// https://youtrack.jetbrains.com/issue/KTIJ-19369
@file:Suppress("DSL_SCOPE_VIOLATION")

buildscript {
   repositories {
      mavenCentral()
   }
   dependencies {
      classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0")
   }
}

// This file is intentionally empty. We should not use any allprojects {} calls, to conform with future project isolation
// See https://gradle.github.io/configuration-cache/#project_isolation
