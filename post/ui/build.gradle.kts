plugins {
   androidLibraryModule
   di
   compose
   navigation
   showkase
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

   testImplementation(projects.post.test)
   testImplementation(libs.kotlinova.core.test)

   androidTestImplementation(projects.post.test)
   androidTestImplementation(libs.androidx.test.core)
   androidTestImplementation(libs.junit4)
}
