import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.accessors.dm.LibrariesForLibs
import util.commonAndroid
import util.isAndroidProject

val libs = the<LibrariesForLibs>()

plugins {
   id("com.github.ben-manes.versions")
   id("io.gitlab.arturbosch.detekt")
}

if (isAndroidProject()) {
   commonAndroid {
      lint {
         lintConfig = file("$rootDir/config/android-lint.xml")
         abortOnError = true

         warningsAsErrors = true
      }
   }
}

tasks.withType<DependencyUpdatesTask> {
   gradleReleaseChannel = "current"

   rejectVersionIf {
      candidate.version.contains("alpha", ignoreCase = true) ||
         candidate.version.contains("beta", ignoreCase = true) ||
         candidate.version.contains("RC", ignoreCase = true) ||
         candidate.version.contains("M", ignoreCase = true) ||
         candidate.version.contains("eap", ignoreCase = true) ||
         candidate.version.contains("dev", ignoreCase = true) ||
         candidate.version.contains("pre", ignoreCase = true)
   }

   reportfileName = "versions"
   outputFormatter = "json"
}

detekt {
   config.setFrom("$rootDir/config/detekt.yml")
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
   reports {
      sarif.required.set(true)
   }

   finalizedBy(":reportMerge")
}

dependencies {
   detektPlugins(libs.detekt.formatting)
   detektPlugins(libs.detekt.compilerWarnings)
   detektPlugins(libs.detekt.twitterCompose)
   detektPlugins(libs.kotlinova.navigation.detekt)
}
