plugins {
   androidLibraryModule
   compose
   screenshotTests
}

android {
   namespace = "si.inova.androidarchitecutreplayground.products.ui"
}

dependencies {
   api(projects.products.data)

   implementation(projects.common.android)
   implementation(projects.navigationCommon)
   implementation(projects.uiCommon)
}
