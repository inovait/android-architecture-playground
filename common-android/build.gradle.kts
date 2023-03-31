plugins {
   androidLibraryModule
}

android {
   namespace = "si.inova.kotlinova.core"
}

dependencies {
   api(projects.common)
   api(libs.kotlinova.core)
}
