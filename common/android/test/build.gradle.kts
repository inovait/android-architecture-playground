plugins {
   androidLibraryModule
}

android {
   namespace = "si.inova.arch.androidarchitectureplayground.test"
}

dependencies {
   api(projects.common.android)
   api(projects.common.pure.test)
   implementation(libs.kotlin.coroutines.test)
   implementation(libs.kotlin.coroutines)
}
