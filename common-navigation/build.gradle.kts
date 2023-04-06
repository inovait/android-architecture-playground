plugins {
   androidLibraryModule
   id("kotlin-parcelize")
   compose
}

android {
   namespace = "si.inova.kotlinova.navigation.common"
}

dependencies {
   api(libs.kotlinova.navigation)
   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.lifecycle.viewModel)
   implementation(libs.androidx.lifecycle.viewModel.compose)
   implementation(libs.dispatch)
   implementation(libs.kotlinova.compose)

}
