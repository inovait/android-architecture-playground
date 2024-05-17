// This file is intentionally blank. Please do not add things here unless really necesary in order to stay compatible with
// project isolation when it comes out (https://gradle.github.io/configuration-cache/#project_isolation)

plugins {
   id("com.autonomousapps.dependency-analysis") version "1.31.0"
}

dependencyAnalysis {
   issues {
      all {
         onUnusedDependencies {
            // Rebugger is always included as a convenience, so it can immediately be used. It's debug only anyway
            exclude("io.github.theapache64:rebugger")

            // Standard test dependencies, included in all modules by default for huge convenience boost
            exclude("org.junit.jupiter:junit-jupiter-api")
            exclude("io.kotest:kotest-assertions-core")
            exclude("org.jetbrains.kotlinx:kotlinx-coroutines-test")
            exclude("app.cash.turbine:turbine")
            exclude("app.cash.turbine:turbine")
         }

         onIncorrectConfiguration  {
            // Showkase is only used by the generated code, by app module that also needs to explicitly include showkase
            exclude("com.airbnb.android:showkase")

            // Dagger runtime annotations are useless without explicit dagger dependency
            exclude("com.google.dagger:dagger")
         }
      }
   }

   structure {
      bundle("coil") {
         // We only ever want coil-compose, so coil is considered as a group
         includeGroup("io.coil-kt")
      }
   }
}
