plugins {
   androidLibraryModule
}

android {
   namespace = "si.inova.androidarchitectureplayground.test"
}

dependencies {
   api(projects.common)
   api(libs.kotlinova.core)
}
