plugins {
   androidLibraryModule
   compose
}

android {
   namespace = "si.inova.archandroidarchitectureplayground.masterdetail"
}

dependencies {
   implementation(projects.commonAndroid)
   implementation(projects.commonNavigation)
   implementation(projects.commonCompose)

   implementation(libs.androidx.compose.material3.sizeClasses)
   implementation(libs.androidx.activity.compose)
}