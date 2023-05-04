plugins {
   androidLibraryModule
   compose
}

android {
   namespace = "si.inova.androidarchitectureplayground.login"
}

dependencies {
   api(projects.login.api)
   implementation(projects.commonNavigation)

   implementation(libs.dispatch)
}
