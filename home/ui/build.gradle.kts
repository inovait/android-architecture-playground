plugins {
   androidLibraryModule
   di
   compose
   navigation
   unmock
}

android {
   namespace = "si.inova.androidarchitectureplayground.home"

   androidResources.enable = true
}

dependencies {
   api(libs.kotlinova.navigation)
   implementation(libs.androidx.compose.material3.sizeClasses)
   implementation(libs.kotlinova.core)
}
