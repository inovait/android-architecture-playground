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
   api(projects.commonCompose)
   api(projects.user.api)

   api(libs.kotlinova.navigation)
   implementation(libs.kotlinova.core)
   implementation(libs.androidx.compose.material)

   testImplementation(projects.user.test)
   testImplementation(libs.kotlinova.core.test)

   androidTestImplementation(projects.user.test)
   androidTestImplementation(libs.junit4)
   androidTestImplementation(libs.kotlin.coroutines)
}
