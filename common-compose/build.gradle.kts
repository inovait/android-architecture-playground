plugins {
   androidLibraryModule
   compose
   parcelize
}

android {
   namespace = "si.inova.androidarchitectureplayground.ui"
}

dependencies {
   api(libs.kotlinova.compose)
   implementation(projects.commonAndroid)
   implementation(projects.commonNavigation)
   implementation(libs.coil)
}
