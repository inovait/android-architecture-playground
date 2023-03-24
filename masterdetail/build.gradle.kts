plugins {
   androidLibraryModule
   compose
}

android {
   namespace = "si.inova.androidarchitectureplayground.masterdetail"
}

dependencies {
   implementation(projects.commonAndroid)
   implementation(projects.commonNavigation)
   implementation(projects.commonCompose)

   implementation(libs.androidx.compose.material3.sizeClasses)
   implementation(libs.androidx.activity.compose)
}
