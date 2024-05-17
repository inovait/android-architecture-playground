plugins {
   androidLibraryModule
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
   implementation(projects.common)
   implementation(projects.commonCompose)
   implementation(projects.user.api)

   implementation(libs.kotlinova.core)
   implementation(libs.androidx.compose.material)

   testImplementation(projects.common.test)
   testImplementation(projects.user.test)

   androidTestImplementation(projects.user.test)
}
