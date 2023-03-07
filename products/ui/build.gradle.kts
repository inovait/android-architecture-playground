plugins {
   androidLibraryModule
   compose
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
