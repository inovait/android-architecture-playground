plugins {
   androidLibraryModule
   compose
}

android {
   namespace = "si.inova.androidarchitectureplayground.login"
}

dependencies {
   implementation(projects.commonNavigation)

   implementation(libs.dispatch)
}
