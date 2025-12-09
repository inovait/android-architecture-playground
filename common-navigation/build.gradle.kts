plugins {
   androidLibraryModule
   compose
   parcelize
}

dependencies {
   api(libs.kotlinova.navigation)
   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.compose.material3.sizeClasses)
   implementation(libs.androidx.navigation3.ui)
   implementation(libs.accompanist.adaptive)
   implementation(libs.kotlinova.core)
   implementation(libs.kotlinova.compose)
   implementation(libs.kotlinova.navigation.navigation3)
}
