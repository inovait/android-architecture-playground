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
   implementation(libs.kotlinova.compose)
   implementation(libs.androidx.compose.material3.sizeClasses)
   implementation(libs.androidx.core)
   implementation(libs.kotlinova.core)
   implementation(libs.coil)
}
