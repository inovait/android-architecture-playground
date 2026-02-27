plugins {
   androidLibraryModule
   di
}

dependencies {
   api(projects.commonRetrofit)
   api(libs.dispatch)
   api(libs.moshi)
   api(libs.okhttp)

   implementation(libs.kotlin.coroutines)
}
