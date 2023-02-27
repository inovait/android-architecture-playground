plugins {
   androidLibraryModule
   id("kotlin-parcelize")
   compose
}

android {
   namespace = "si.inova.androidarchitectureplayground.navigation.common"
}

dependencies {
   api(libs.simpleStack)
   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.lifecycle.viewModel)
   implementation(libs.androidx.lifecycle.viewModel.compose)

}
