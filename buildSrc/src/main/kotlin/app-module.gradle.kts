import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.dependencies

val libs = the<LibrariesForLibs>()

plugins {
   id("com.android.application")
   id("android-module-commons")
   id("kotlin-kapt")
   id("toml-update")
}

anvil {
   syncGeneratedSources.set(true)
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
