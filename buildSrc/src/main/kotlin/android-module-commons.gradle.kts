import util.commonAndroid
import util.commonKotlinOptions

plugins {
   id("org.jetbrains.kotlin.android")

   id("checks")
   id("org.gradle.android.cache-fix")
}

val customConfig = extensions.create<CustomBuildConfiguration>("custom")

commonAndroid {
   compileSdk = 33

   compileOptions {
      sourceCompatibility = JavaVersion.VERSION_11
      targetCompatibility = JavaVersion.VERSION_11
   }

   commonKotlinOptions {
      jvmTarget = "11"
   }

   defaultConfig {
      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
   }

   packagingOptions {
      resources {
         excludes += "/META-INF/{AL2.0,LGPL2.1}"
      }
   }
}

// Even empty android test tasks take a while to execute. Disable all of them by default.
tasks.configureEach {
   if (!customConfig.enableEmulatorTests.getOrElse(false) && name.contains("AndroidTest", ignoreCase = true)) {
      enabled = false
   }
}
