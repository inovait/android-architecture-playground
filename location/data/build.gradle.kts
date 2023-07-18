plugins {
   androidLibraryModule
}

dependencies {
   api(projects.location.api)

   implementation(projects.common)
   implementation(libs.playServices.location)
   implementation(libs.kotlin.coroutines.playServices)
}
