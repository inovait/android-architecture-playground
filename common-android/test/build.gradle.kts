plugins {
   androidLibraryModule
}

android {
   namespace = "si.inova.kotlinova.coreTest"
}

dependencies {
   api(projects.commonAndroid)
   api(projects.common.test)
   implementation(libs.kotlin.coroutines.test)
   implementation(libs.kotlin.coroutines)
}
