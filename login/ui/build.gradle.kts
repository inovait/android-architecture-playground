plugins {
   androidLibraryModule
   di
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
   api(projects.common)
   api(projects.commonCompose)
   api(projects.login.api)
   api(libs.kotlin.coroutines)
   api(libs.kotlinova.core)
   api(libs.kotlinova.navigation)

   testImplementation(projects.login.test)
   testImplementation(libs.kotlinova.core.test)
}
