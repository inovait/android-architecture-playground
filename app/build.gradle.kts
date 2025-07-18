import com.slack.keeper.optInToKeeper

plugins {
   androidAppModule
   compose
   navigation
   parcelize
   showkase
   id("com.slack.keeper")
   id("androidx.baselineprofile")
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
      versionName = "1.0.0"

      testInstrumentationRunner = "si.inova.androidarchitectureplayground.instrumentation.TestRunner"
      testInstrumentationRunnerArguments += "clearPackageData" to "true"
      // Needed to enable test coverage
      testInstrumentationRunnerArguments += "useTestStorageService" to "true"
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

androidComponents {
   beforeVariants { builder ->
      if (builder.name.contains("proguardedDebug")) {
         builder.optInToKeeper()
      }
   }
}

keeper {
   automaticR8RepoManagement = false
}

custom {
   enableEmulatorTests.set(true)
}

dependencies {
   implementation(projects.common)
   implementation(projects.commonNavigation)
   implementation(projects.commonRetrofit)
   implementation(projects.commonRetrofit.android)
   implementation(projects.commonCompose)

   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.core)
   implementation(libs.androidx.core.splashscreen)
   implementation(libs.androidx.lifecycle.runtime)
   implementation(libs.androidx.lifecycle.viewModel)
   implementation(libs.androidx.lifecycle.viewModel.compose)
   implementation(libs.coil)
   implementation(libs.dispatch)
   implementation(libs.kotlin.immutableCollections)
   implementation(libs.moshi)
   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
   implementation(libs.kotlinova.navigation)
   implementation(libs.kotlinova.retrofit)
   implementation(libs.simpleStack)

   implementation(libs.androidx.datastore)
   implementation(libs.androidx.datastore.preferences)

   debugImplementation(libs.whatTheStack)

   androidTestImplementation(libs.androidx.test.junitRules)
   androidTestImplementation(libs.androidx.test.runner)
   androidTestImplementation(libs.junit4)
   androidTestImplementation(libs.kotlinova.retrofit.test)
   androidTestImplementation(libs.okhttp)
   androidTestImplementation(libs.okhttp.mockWebServer)
   androidTestImplementation(libs.certificateTransparency)
   androidTestUtil(libs.androidx.test.orchestrator)
   androidTestUtil(libs.androidx.test.services)

   keeperR8(libs.androidx.r8)

   add("benchmarkRuntimeOnly", libs.androidx.profileInstaller)
   add("benchmarkRuntimeOnly", libs.androidx.compose.tracing)
   add("baselineProfile", projects.appBenchmark)
}
