plugins {
   androidLibraryModule
   di
   compose
   navigation
   showkase
}

android {
   namespace = "si.inova.architectureplayground.user"

   buildFeatures {
      androidResources = true
   }
}

custom {
   enableEmulatorTests.set(true)
}

dependencies {
   api(projects.common)
   api(projects.user.api)

   implementation(projects.commonCompose)
   api(libs.kotlinova.navigation)
   implementation(libs.kotlinova.core)

   testImplementation(projects.user.test)
   testImplementation(libs.kotlinova.core.test)

   androidTestImplementation(projects.user.test)
   androidTestImplementation(libs.junit4)
   androidTestImplementation(libs.kotlin.coroutines)
}
