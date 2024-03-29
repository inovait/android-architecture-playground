plugins {
   androidLibraryModule
   compose
   navigation
   parcelize
}

android {
   namespace = "si.inova.androidarchitectureplayground.login.ui"

   buildFeatures {
      androidResources = true
      buildConfig = true
   }
}

dependencies {
   implementation(projects.common)
   implementation(projects.commonCompose)
   implementation(projects.login.api)

   testImplementation(projects.login.test)
   androidTestImplementation(projects.login.test)
}
