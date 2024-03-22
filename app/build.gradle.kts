import com.android.build.api.variant.BuildConfigField

plugins {
   androidAppModule
   compose
   navigation
   parcelize
   sqldelight
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
      versionName = "1.0-${System.getenv("BUILD_NUMBER") ?: "local"}"

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

         signingConfig = signingConfigs.getByName("debug")
      }
   }
}

sqldelight {
   databases {
      create("Database") {
         packageName.set("si.inova.androidarchitectureplayground")
         dependency(projects.user.data)
         dependency(projects.post.data)
      }
   }
}

custom {
   enableEmulatorTests.set(true)
}

dependencies {
   implementation(projects.commonAndroid)
   implementation(projects.commonNavigation)
   implementation(projects.commonRetrofit.android)
   implementation(projects.commonCompose)
   implementation(projects.login.data)
   implementation(projects.login.ui)
   implementation(projects.home.ui)
   implementation(projects.user.data)
   implementation(projects.user.ui)
   implementation(projects.post.data)
   implementation(projects.post.ui)

   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.core)
   implementation(libs.androidx.core.splashscreen)
   implementation(libs.androidx.lifecycle.runtime)
   implementation(libs.androidx.lifecycle.viewModel)
   implementation(libs.androidx.lifecycle.viewModel.compose)
   implementation(libs.certificateTransparency)
   implementation(libs.dispatch)
   implementation(libs.kotlin.immutableCollections)
   implementation(libs.retrofit.moshi)
   implementation(libs.simpleStack)
   implementation(libs.sqldelight.android)

   implementation(libs.androidx.datastore)
   implementation(libs.androidx.datastore.preferences)

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
