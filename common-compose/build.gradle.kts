plugins {
   androidLibraryModule
   compose
   showkase
}

android {
   namespace = "si.inova.androidarchitectureplayground.common.compose"

   androidResources.enable = true
}

dependencies {
   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
   implementation(libs.kotlinova.compose)
   implementation(libs.coil)
   implementation(libs.coil.okhttp)
}
