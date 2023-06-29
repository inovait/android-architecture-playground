plugins {
   androidLibraryModule
   compose
   navigation
}

android {
   namespace = "si.inova.architectureplayground.post"

   buildFeatures {
      androidResources = true
   }
}

dependencies {
   implementation(projects.common)
   implementation(projects.commonCompose)
   implementation(projects.post.api)

   implementation(libs.kotlinova.core)
   implementation(libs.androidx.compose.material)
   implementation(libs.coil)

   testImplementation(projects.post.test)

   androidTestImplementation(projects.post.test)
}