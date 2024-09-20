plugins {
   androidLibraryModule
   compose
   parcelize
}

dependencies {
   api(libs.kotlinova.navigation)
   implementation(libs.androidx.activity.compose)
   implementation(libs.kotlinova.compose)
}
