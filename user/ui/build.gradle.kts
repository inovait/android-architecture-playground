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
   androidTestImplementation(libs.junit4)
   androidTestImplementation(libs.kotlin.coroutines)
}
