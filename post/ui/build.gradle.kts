plugins {
   androidLibraryModule
   di
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
   api(projects.common)
   api(projects.commonCompose)
   api(projects.post.api)

   api(libs.kotlin.coroutines)
   api(libs.kotlinova.navigation)
   implementation(libs.kotlinova.core)
   implementation(libs.androidx.activity.compose)
   implementation(libs.coil)

   testImplementation(projects.post.test)
   testImplementation(libs.kotlinova.core.test)

   androidTestImplementation(projects.post.test)
   androidTestImplementation(libs.junit4)
}
