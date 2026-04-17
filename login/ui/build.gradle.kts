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
   implementation(projects.common)
   implementation(projects.commonCompose)
   implementation(projects.home.api)
   implementation(projects.login.api)
   implementation(projects.post.api)
   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
   implementation(libs.kotlinova.navigation)

   testImplementation(testFixtures(projects.login.api))
   testImplementation(libs.kotlinova.core.test)
}
