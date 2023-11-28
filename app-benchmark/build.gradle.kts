plugins {
   id("com.android.test")
   commonAndroid
}

android {
   namespace = "si.inova.androidarchitectureplayground.benchmark"

   buildTypes {
      create("benchmark") {
         isDebuggable = true
         signingConfig = getByName("debug").signingConfig
         matchingFallbacks += listOf("release")
      }
   }

   targetProjectPath = ":app"
   experimentalProperties["android.experimental.self-instrumenting"] = true
}

custom {
   enableEmulatorTests = true
}

dependencies {
   implementation(libs.junit4)
   implementation(libs.androidx.test.espresso)
   implementation(libs.androidx.test.uiautomator)
   implementation(libs.androidx.benchmark.macro.junit4)
   implementation(libs.androidx.profileInstaller)
}

androidComponents {
   beforeVariants(selector().all()) {
      it.enable = it.buildType == "benchmark"
   }
}
