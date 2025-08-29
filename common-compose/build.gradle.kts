plugins {
   androidLibraryModule
   compose
   parcelize
   showkase
}

android {
   namespace = "si.inova.androidarchitectureplayground.ui"

   buildFeatures {
      androidResources = true
   }
}

dependencies {
   implementation(libs.kotlinova.core)
   implementation(libs.kotlinova.compose)
   implementation(libs.coil)
   implementation(libs.coil.okhttp)
}
