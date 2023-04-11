import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
   id("com.android.application")
   id("android-module-commons")
   id("kotlin-kapt")
   id("toml-update")
   id("com.jraska.module.graph.assertion")
}

anvil {
   syncGeneratedSources.set(true)
}

moduleGraphAssert {
   maxHeight = 6
   restricted = arrayOf(":common-navigation -X> .*")
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
