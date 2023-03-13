plugins {
   androidLibraryModule
   compose
}

android {
   namespace = "si.inova.archandroidarchitectureplayground.masterdetail"
}

dependencies {
   implementation(projects.common.android)
   implementation(projects.navigationCommon)
   implementation(projects.uiCommon)

   implementation(libs.androidx.compose.material3.sizeClasses)
   implementation(libs.androidx.activity.compose)
}
