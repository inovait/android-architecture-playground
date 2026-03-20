plugins {
   androidLibraryModule
   di
   compose
   instrumentedTests
   navigation
   showkase
   testFixtures
}

android {
   namespace = "si.inova.architectureplayground.post"

   androidResources.enable = true
}

dependencies {
   api(projects.user.api)

   implementation(projects.common)
   implementation(projects.commonCompose)
   implementation(projects.post.api)
   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.compose.material3.sizeClasses)
   implementation(libs.coil)
   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
   implementation(libs.kotlinova.navigation)

   testImplementation(testFixtures(projects.post.api))
   testImplementation(libs.kotlinova.core.test)

   androidTestImplementation(testFixtures(projects.post.api))
   androidTestImplementation(libs.androidx.test.core)
   // We don't need espresso directly, but we need to force a higher version to work on the Android 16 QPR2
   androidTestImplementation(libs.androidx.test.espresso)
   androidTestImplementation(libs.junit4)
}
