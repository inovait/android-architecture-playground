import com.android.build.api.dsl.LibraryBuildFeatures
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
   // Use default namespace for no resources, modules that use resources must override this
   namespace = "si.inova.androidarchitectureplayground.noresources"

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

   buildFeatures {
      buildConfig = false
      resValues = false
      shaders = false

      if (this is LibraryBuildFeatures) {
         androidResources = false
      }
   }
}

kotlin {
   jvmToolchain(17)
}

dependencies {
   add("coreLibraryDesugaring", libs.desugarJdkLibs)

   add("androidTestImplementation", libs.kotest.assertions)
}
