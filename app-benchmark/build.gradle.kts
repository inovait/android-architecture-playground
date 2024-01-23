import si.inova.kotlinova.gradle.benchmarkupload.GoogleCloudBenchmarkUpload

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

   defaultConfig {
      testInstrumentationRunnerArguments["androidx.benchmark.perfettoSdkTracing.enable"] = "true"
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
   implementation(libs.androidx.perfetto)
   implementation(libs.androidx.perfetto.binary)
   implementation(libs.kotest.assertions)
}

androidComponents {
   beforeVariants(selector().all()) {
      it.enable = it.buildType == "benchmark"
   }
}

val benchmarkResultsUpload by tasks.registering(GoogleCloudBenchmarkUpload::class) {
   googleCloudProjectId = "android-architecture-playground"

   benchmarkResultFiles.from(
      fileTree("results") {
         include("*-portrait/artifacts/sdcard/Download/*-benchmarkData.json")
      }
   )

   metricMap.putAll(
      mapOf(
         "JIT compiling %Count" to "jit_count",
         "JIT compiling %Ms" to "jit_duration",
         "frameDurationCpuMs" to "frame_duration",
         "timeToFullDisplayMs" to "time_to_full_display",
         "timeToInitialDisplayMs" to "time_to_initial_display",
      )
   )
}
