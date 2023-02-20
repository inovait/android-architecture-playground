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
}
