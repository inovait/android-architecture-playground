import com.android.build.api.variant.BuildConfigField

plugins {
   androidAppModule
   compose
   navigation
   parcelize
   showkase
}

android {
   namespace = "si.inova.androidarchitectureplayground"

   buildFeatures {
      buildConfig = true
   }

   defaultConfig {
      applicationId = "si.inova.androidarchitectureplayground"
      targetSdk = 33
      versionCode = 1
      versionName = "1.0"

      testInstrumentationRunner = "si.inova.androidarchitectureplayground.instrumentation.TestRunner"
      testInstrumentationRunnerArguments += "clearPackageData" to "true"

      androidComponents {
         onVariants {
            it.buildConfigFields.put(
               "GIT_HASH",
               providers.exec {
                  commandLine("git", "rev-parse", "--short", "HEAD")
               }.standardOutput.asText.map { it.trim() }.map { gitHash ->
                  BuildConfigField(
                     "String",
                     "\"$gitHash\"",
                     "Git Version"
                  )
               }
            )
         }
      }
   }

   testOptions {
      execution = "ANDROIDX_TEST_ORCHESTRATOR"
   }

   if (hasProperty("testAppWithProguard")) {
      testBuildType = "proguardedDebug"
   }

   signingConfigs {
      getByName("debug") {
         // SHA1: TODO
         // SHA256: TODO

         storeFile = File(rootDir, "keys/debug.jks")
         storePassword = "android"
         keyAlias = "androiddebugkey"
         keyPassword = "android"
      }

      create("release") {
         // SHA1: TODO
         // SHA256: TODO

         storeFile = File(rootDir, "keys/release.jks")
         storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
         keyAlias = "app"
         keyPassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
      }
   }

   buildTypes {
      getByName("debug") {
         // TODO uncomment when above signing config becomes valid
         // signingConfig = signingConfigs.getByName("debug")
      }

      create("proguardedDebug") {
         isMinifyEnabled = true
         isShrinkResources = true

         proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro",
            "proguard-rules-test.pro"
         )

         testProguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro",
            "proguard-rules-test.pro"
         )

         matchingFallbacks += "debug"

         signingConfig = signingConfigs.getByName("debug")
      }

      create("benchmark") {
         isDebuggable = true
         initWith(buildTypes.getByName("release"))
         signingConfig = signingConfigs.getByName("debug")
         matchingFallbacks += listOf("release")
      }

      getByName("release") {
         isMinifyEnabled = true
         isShrinkResources = true

         proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
         )

         signingConfig = signingConfigs.getByName("release")
      }
   }
}

dependencies {
   implementation(projects.commonAndroid)
   implementation(projects.commonNavigation)
   implementation(projects.commonRetrofit.android)
   implementation(projects.commonCompose)

   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.core)
   implementation(libs.androidx.core.splashscreen)
   implementation(libs.androidx.lifecycle.runtime)
   implementation(libs.androidx.lifecycle.viewModel)
   implementation(libs.androidx.lifecycle.viewModel.compose)
   implementation(libs.coil)
   implementation(libs.certificateTransparency)
   implementation(libs.dispatch)
   implementation(libs.kotlin.immutableCollections)
   implementation(libs.retrofit.moshi)
   implementation(libs.simpleStack)

   implementation(libs.androidx.datastore)
   implementation(libs.androidx.datastore.preferences)

   debugImplementation(libs.whatTheStack)

   testImplementation(projects.commonAndroid.test)
   androidTestImplementation(projects.commonAndroid.test)
   androidTestImplementation(projects.commonRetrofit.test)
   androidTestImplementation(libs.dispatch.espresso)
   androidTestImplementation(libs.kotlinova.retrofit.test)
   androidTestImplementation(libs.kotlinova.compose.androidTest)
   androidTestImplementation(libs.androidx.test.runner)
   androidTestUtil(libs.androidx.test.orchestrator)
   testImplementation(libs.junit4)

   add("benchmarkImplementation", libs.androidx.profileInstaller)
   add("benchmarkImplementation", libs.androidx.compose.tracing)
}
