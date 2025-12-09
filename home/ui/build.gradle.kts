plugins {
   androidLibraryModule
   di
   compose
   navigation
   unmock
   showkase
}

android {
   namespace = "si.inova.androidarchitectureplayground.home"

   androidResources.enable = true
}

dependencies {
   api(libs.kotlinova.navigation)
   implementation(projects.commonCompose)
   implementation(libs.androidx.compose.material3.sizeClasses)
   implementation(libs.kotlinova.core)
}
