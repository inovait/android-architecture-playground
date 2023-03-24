plugins {
   androidLibraryModule
   compose
   screenshotTests
}

android {
   namespace = "si.inova.androidarchitectureplayground.products.ui"
}

dependencies {
   api(projects.products.data)

   implementation(projects.commonAndroid)
   implementation(projects.commonNavigation)
   implementation(projects.commonCompose)
}
