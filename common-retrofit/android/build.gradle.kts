plugins {
   androidLibraryModule
}

android {
   namespace = "si.inova.androidarchitectureplayground.network.android"
}

dependencies {
   api(projects.commonRetrofit)
   api(libs.kotlinova.retrofit)

   implementation(projects.commonAndroid)
   implementation(libs.dispatch)
   implementation(libs.certificateTransparency.android)
}
