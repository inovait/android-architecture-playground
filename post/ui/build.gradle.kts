plugins {
   androidLibraryModule
   compose
   navigation
   showkase
}

android {
   namespace = "si.inova.architectureplayground.post"

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
   implementation(projects.post.api)

   implementation(libs.kotlinova.core)
   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.compose.material)
   implementation(libs.coil)

   testImplementation(projects.post.test)

   androidTestImplementation(projects.post.test)
}
