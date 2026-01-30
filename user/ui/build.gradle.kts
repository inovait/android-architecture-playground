plugins {
   androidLibraryModule
   di
   compose
   navigation
   showkase
}

android {
   namespace = "si.inova.architectureplayground.user"

   androidResources.enable = true
}

custom {
   enableEmulatorTests.set(true)
}

dependencies {
   api(projects.common)
   api(projects.user.api)

   implementation(projects.commonCompose)
   implementation(projects.commonPaging)
   api(libs.kotlinova.navigation)
   implementation(libs.androidx.compose.material3.sizeClasses)
   implementation(libs.androidx.paging.common)
   implementation(libs.kotlinova.core)

   testImplementation(testFixtures(projects.user.api))
   testImplementation(libs.kotlinova.core.test)

   androidTestImplementation(testFixtures(projects.user.api))
   androidTestImplementation(libs.androidx.test.core)
   // We don't need espresso directly, but we need to force a higher version to work on the Android 16 QPR2
   androidTestImplementation(libs.androidx.test.espresso)
   androidTestImplementation(libs.junit4)
   androidTestImplementation(libs.kotlin.coroutines)
}
