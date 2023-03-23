plugins {
   androidLibraryModule
   compose
}

android {
   namespace = "si.inova.arch.CHANGE_ME"
}

dependencies {
   implementation(projects.commonNavigation)

   implementation(libs.dispatch)
}
