import com.slack.keeper.optInToKeeper

plugins {
   androidAppModule
   compose
   navigation
   parcelize
   sqldelight
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
         // SHA1: CA:85:CF:46:B9:D8:39:1B:DE:23:6C:4C:93:68:26:8D:BD:C6:6E:1D
         // SHA256: 50:56:00:D6:EF:49:A1:59:C3:DD:10:17:EF:50:A5:6C:E0:5E:CF:CF:D7:4F:3D:E2:3E:80:8E:47:A8:3D:5A:67

         storeFile = File(rootDir, "keys/debug.jks")
         storePassword = "android"
         keyAlias = "androiddebugkey"
         keyPassword = "android"
      }
   }

   buildTypes {
      getByName("debug") {
         signingConfig = signingConfigs.getByName("debug")
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

         signingConfig = signingConfigs.getByName("debug")
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

sqldelight {
   databases {
      create("Database") {
         packageName.set("si.inova.androidarchitectureplayground")
         schemaOutputDirectory.set(file("src/main/sqldelight/databases"))

         // Use project() wrapper as a workaround for the https://github.com/sqldelight/sqldelight/pull/5801
         dependency(project(projects.user.data.path))
         dependency(project(projects.post.data.path))
      }
   }
}

afterEvaluate {
   tasks.named("verifyDebugDatabaseMigration") {
      // Workaround for the https://github.com/cashapp/sqldelight/issues/5115
      mustRunAfter("generateDebugDatabaseSchema")
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
   implementation(projects.login.api)
   implementation(projects.login.data)
   implementation(projects.login.ui)
   implementation(projects.navigationImpl)
   implementation(projects.home.ui)
   implementation(projects.user.api)
   implementation(projects.user.data)
   implementation(projects.user.ui)
   implementation(projects.post.api)
   implementation(projects.post.data)
   implementation(projects.post.ui)

   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.core)
   implementation(libs.androidx.core.splashscreen)
   implementation(libs.androidx.lifecycle.runtime)
   implementation(libs.androidx.lifecycle.viewModel)
   implementation(libs.androidx.lifecycle.viewModel.compose)
   implementation(libs.androidx.navigation3)
   implementation(libs.androidx.navigation3)
   implementation(libs.coil)
   implementation(libs.dispatch)
   implementation(libs.kotlinova.retrofit)
   implementation(libs.kotlin.immutableCollections)
   implementation(libs.logcat)
   implementation(libs.moshi)
   implementation(libs.okhttp)
   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
   implementation(libs.kotlinova.navigation)
   implementation(libs.kotlinova.navigation.navigation3)
   implementation(libs.simpleStack)
   implementation(libs.sqldelight.android)

   implementation(libs.androidx.datastore)
   implementation(libs.androidx.datastore.preferences)

   debugImplementation(libs.whatTheStack)

   androidTestImplementation(libs.androidx.test.junitRules)
   androidTestImplementation(libs.androidx.test.runner)
   androidTestImplementation(libs.junit4)
   androidTestImplementation(libs.kotlinova.retrofit.test)
   androidTestImplementation(libs.okhttp.mockWebServer)
   androidTestUtil(libs.androidx.test.orchestrator)
   androidTestUtil(libs.androidx.test.services)

   keeperR8(libs.androidx.r8)

   add("benchmarkRuntimeOnly", libs.androidx.profileInstaller)
   add("benchmarkRuntimeOnly", libs.androidx.compose.tracing)
   add("baselineProfile", projects.appBenchmark)
}
