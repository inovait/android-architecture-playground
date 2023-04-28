import org.gradle.accessors.dm.LibrariesForLibs
import util.commonAndroid
import util.commonKotlinOptions

val libs = the<LibrariesForLibs>()

plugins {
   id("org.jetbrains.kotlin.android")

   id("all-modules-commons")
   id("org.gradle.android.cache-fix")
}

commonAndroid {
   compileSdk = 33

   compileOptions {
      sourceCompatibility = JavaVersion.VERSION_17
      targetCompatibility = JavaVersion.VERSION_17

      isCoreLibraryDesugaringEnabled = true
   }

   commonKotlinOptions {
      freeCompilerArgs += "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
      freeCompilerArgs += "-opt-in=kotlinx.coroutines.FlowPreview"
   }

   defaultConfig {
      minSdk = 24

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
   }

   testOptions {
      unitTests.all {
         it.useJUnitPlatform()
      }
   }

   packaging {
      resources {
         excludes += "/META-INF/{AL2.0,LGPL2.1}"
      }
   }
}

kotlin {
   jvmToolchain(17)
}

dependencies {
   add("implementation", libs.whetstone.runtime.get().toString()) {
      // Workaround for the https://github.com/deliveryhero/whetstone/pull/81
      exclude(module = "appcompat")
   }
   add("anvil", libs.whetstone.compiler)
   add("coreLibraryDesugaring", libs.desugarJdkLibs)

   add("androidTestImplementation", libs.kotest.assertions)
}
