plugins {
   id("com.android.test")
   commonAndroid
}

// HACK alert:

// Various emulator providers (such as Firebase test lab) bill us per-apk. To reduce costs, we merge all instrumented tests
// in our app into a single APK using this module and then only send this module to the Firebase

android {
   namespace = "si.inova.androidarchitectureplayground.instrumented"

   targetProjectPath = ":app"

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

   defaultConfig {
      targetSdk = 33

      testInstrumentationRunner = "si.inova.androidarchitectureplayground.instrumentation.TestRunner"
      testInstrumentationRunnerArguments += "clearPackageData" to "true"
      // Needed to enable test coverage
      // testInstrumentationRunnerArguments += "useTestStorageService" to "true"
   }

   testOptions {
      execution = "ANDROIDX_TEST_ORCHESTRATOR"
   }
}

custom {
   enableEmulatorTests = true
}

dependencies {
   implementation(libs.junit4)

   implementation(project(path = projects.app.path, configuration = "androidTestClasses"))
   implementation(project(path = projects.post.ui.path, configuration = "androidTestClasses"))
   implementation(project(path = projects.user.ui.path, configuration = "androidTestClasses"))

   androidTestUtil(libs.androidx.test.orchestrator)
   androidTestUtil(libs.androidx.test.services)
}
