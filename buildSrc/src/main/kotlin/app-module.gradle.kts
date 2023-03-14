import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.dependencies

val libs = the<LibrariesForLibs>()

plugins {
   id("com.android.application")
   id("android-module-commons")
   id("kotlin-kapt")
}

anvil {
   syncGeneratedSources.set(true)
}

dependencies {
   implementation(libs.dagger.runtime)
   implementation(libs.whetstone.runtime)
   anvil(libs.whetstone.compiler)
   kapt(libs.dagger.compiler)
   kaptAndroidTest(libs.dagger.compiler)
   androidTestImplementation(libs.androidx.test.runner)
}
