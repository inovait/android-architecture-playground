plugins {
   androidLibraryModule
   di
   compose
   navigation
   showkase
   testFixtures
}

android {
   namespace = "si.inova.architectureplayground.post"

   androidResources.enable = true
}

custom {
   enableEmulatorTests.set(true)
}

dependencies {
   api(projects.common)
   api(projects.post.api)
   implementation(projects.commonCompose)

   api(libs.kotlin.coroutines)
   api(libs.kotlinova.navigation)
   implementation(libs.kotlinova.core)
   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.compose.material3.sizeClasses)
   implementation(libs.coil)

   testImplementation(testFixtures(projects.post.api))
   testImplementation(libs.kotlinova.core.test)

   androidTestImplementation(testFixtures(projects.post.api))
   androidTestImplementation(libs.androidx.test.core)
   androidTestImplementation(libs.junit4)
}
