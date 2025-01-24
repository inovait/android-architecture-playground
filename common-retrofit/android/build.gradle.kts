plugins {
   androidLibraryModule
   di
}

dependencies {
   api(projects.commonRetrofit)
   api(libs.dispatch)
   api(libs.moshi)
   api(libs.okhttp)
   api(libs.certificateTransparency)

   implementation(libs.kotlin.coroutines)
   implementation(libs.certificateTransparency.android)
}
