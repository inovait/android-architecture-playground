plugins {
   androidLibraryModule
}

android {
   namespace = "si.inova.arch.androidarchitectureplayground.network.android"
}

dependencies {
   api(projects.commonRetrofit)

   implementation(projects.commonAndroid)
   implementation(libs.dispatch)
}
