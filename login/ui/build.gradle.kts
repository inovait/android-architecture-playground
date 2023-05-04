plugins {
   androidLibraryModule
   compose
   navigation
   parcelize
}

android {
   namespace = "si.inova.androidarchitectureplayground.login.ui"
}

dependencies {
   implementation(projects.common)
   implementation(projects.login.api)

   testImplementation(projects.login.test)
   androidTestImplementation(projects.login.test)
}
