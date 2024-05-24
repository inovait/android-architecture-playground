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
   api(libs.kotlinova.navigation)

   implementation(libs.kotlinova.compose)
   implementation(libs.androidx.compose.material3.sizeClasses)
   implementation(libs.androidx.core)
   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
   implementation(libs.androidx.activity.compose)
   implementation(libs.coil)
}
