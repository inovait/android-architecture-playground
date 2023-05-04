plugins {
   androidLibraryModule
   compose
   navigation
}

android {
   namespace = "si.inova.androidarchitectureplayground.home"
}

dependencies {
   implementation(libs.androidx.compose.material3.sizeClasses)
   implementation(libs.kotlinova.core)
}
