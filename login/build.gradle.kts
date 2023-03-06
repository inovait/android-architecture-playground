plugins {
   androidLibraryModule
   compose
}

android {
   namespace = "si.inova.androidarchitectureplayground.login"
}

dependencies {
   implementation(projects.navigationCommon)

   implementation(libs.dispatch)
}
