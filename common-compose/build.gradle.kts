plugins {
   androidLibraryModule
   compose
   parcelize
}

android {
   namespace = "si.inova.androidarchitectureplayground.ui"
}

dependencies {
   implementation(projects.commonAndroid)
   implementation(projects.commonNavigation)
}
