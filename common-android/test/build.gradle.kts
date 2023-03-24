plugins {
   androidLibraryModule
}

android {
   namespace = "si.inova.androidarchitectureplayground.test"
}

dependencies {
   api(projects.commonAndroid)
   api(projects.common.test)
   implementation(libs.kotlin.coroutines.test)
   implementation(libs.kotlin.coroutines)
}
