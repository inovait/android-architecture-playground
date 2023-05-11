plugins {
   androidLibraryModule
   compose
   navigation
}

android {
   namespace = "si.inova.androidarchitectureplayground.home"

   buildFeatures {
      androidResources = true
   }
}

dependencies {
   implementation(libs.androidx.compose.material3.sizeClasses)
   implementation(libs.kotlinova.core)
}
