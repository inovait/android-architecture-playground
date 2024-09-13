import nl.littlerobots.vcu.plugin.resolver.ModuleVersionCandidate
import nl.littlerobots.vcu.plugin.versionSelector

// Please do not add things here unless really necesary in order to stay compatible with
// project isolation when it comes out (https://gradle.github.io/configuration-cache/#project_isolation)

plugins {
   id("com.autonomousapps.dependency-analysis")
   alias(libs.plugins.versionCatalogUpdate)
}

dependencyAnalysis {
   structure {
      ignoreKtx(true)

      bundle("coil") {
         // We only ever want coil-compose, so coil is considered as a group
         includeGroup("io.coil-kt")
      }

      bundle("compose") {
         // Compose libraries are blanket-included to for convenience. It shouldn't cause a big issue
         includeGroup("androidx.compose.animation")
         includeGroup("androidx.compose.foundation")
         includeGroup("androidx.compose.material")
         includeGroup("androidx.compose.material3")
         includeGroup("androidx.compose.runtime")
         includeGroup("androidx.compose.ui")
      }

      // Library Groups:

      bundle("androidxActivity") {
         includeGroup("androidx.activity")
      }

      bundle("androidxCore") {
         includeGroup("androidx.core")
      }

      bundle("androidxLifecycle") {
         includeGroup("androidx.lifecycle")
      }

      bundle("anvil") {
         includeGroup("com.squareup.anvil")
      }

      bundle("datastore") {
         includeGroup("androidx.datastore")
      }

      bundle("kotest") {
         includeGroup("io.kotest")
      }

      bundle("showkase") {
         includeGroup("com.airbnb.android")
      }

      bundle("sqlDelight") {
         includeGroup("app.cash.sqldelight")
      }

      bundle("dagger") {
         includeGroup("com.google.dagger")
         includeDependency("javax.inject:javax.inject")
      }
   }
}

versionCatalogUpdate {
   catalogFile.set(file("config/libs.toml"))

   fun ModuleVersionCandidate.newlyContains(keyword: String): Boolean {
      return !currentVersion.contains(keyword, ignoreCase = true) && candidate.version.contains(keyword, ignoreCase = true)
   }

   versionSelector {
      !it.newlyContains("alpha") &&
         !it.newlyContains("beta") &&
         !it.newlyContains("RC") &&
         !it.newlyContains("M") &&
         !it.newlyContains("eap") &&
         !it.newlyContains("dev") &&
         !it.newlyContains("pre")
   }
}
