import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
   id("com.android.application")
   id("android-module-commons")
   id("kotlin-kapt")
   id("toml-version-bump")
   id("com.jraska.module.graph.assertion")
}

anvil {
   syncGeneratedSources.set(true)
}

tomlVersionBump {
   versionReportFiles.set(
      fileTree(rootDir).apply {
         include("**/build/dependencyUpdates/versions.json")
      }
   )

   tomlFile.set(File(rootDir, "config/libs.toml"))
}

moduleGraphAssert {
   maxHeight = 6
   restricted = arrayOf(
      ":common-navigation -X> .*",
      ":(?!$name).* -X> .*:login"
   )
}

// Workaround for the https://github.com/square/anvil/issues/693
afterEvaluate {
   tasks.named("kaptGenerateStubsDebugKotlin") {
      outputs.upToDateWhen { false }
   }
}

dependencies {
   implementation(libs.dagger.runtime)
   add("implementation", libs.whetstone.runtime.get().toString()) {
      // Workaround for the https://github.com/deliveryhero/whetstone/pull/81
      exclude(module = "appcompat")
   }
   anvil(libs.whetstone.compiler)
   kapt(libs.dagger.compiler)
   kaptAndroidTest(libs.dagger.compiler)
   androidTestImplementation(libs.androidx.test.runner)
}
