plugins {
   androidLibraryModule
}

android {
   namespace = "si.inova.androidarchitectureplayground.common"
}

dependencies {
   api(projects.common)
   compileOnly(libs.androidx.compose.runtime)
}
