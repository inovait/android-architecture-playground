plugins {
   androidLibraryModule
}

android {
   namespace = "si.inova.arch.androidarchitectureplayground.network.android"
}

dependencies {
   api(projects.networkCommon)

   implementation(projects.common.android)
   implementation(libs.dispatch)
}
