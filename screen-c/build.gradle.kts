plugins {
   androidLibraryModule
   compose
}

android {
   namespace = "si.inova.androidarchitectureplayground.screenc"
}

dependencies {
   implementation(projects.commonNavigation)

   implementation(libs.dispatch)
}
