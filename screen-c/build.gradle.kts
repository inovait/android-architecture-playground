plugins {
   androidLibraryModule
   compose
}

android {
   namespace = "si.inova.arch.CHANGE_ME"
}

dependencies {
   implementation(projects.navigationCommon)

   implementation(libs.dispatch)
}
