plugins {
   androidLibraryModule
   di
   compose
   navigation
   serialization
}

android {
   namespace = "si.inova.androidarchitectureplayground.login.ui"

   androidResources.enable = true
   buildFeatures {
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
   implementation(projects.home.api)
   implementation(projects.post.api)

   testImplementation(testFixtures(projects.login.api))
   testImplementation(libs.kotlinova.core.test)
}
