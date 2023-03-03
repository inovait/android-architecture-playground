plugins {
   androidLibraryModule
}

android {
   namespace = "si.inova.androidarchitectureplayground.common"
}

dependencies {
   api(projects.common.pure)
}
