plugins {
   androidLibraryModule
}

dependencies {
   api(projects.commonAndroid)
   implementation(libs.kotlin.coroutines.test)
}
