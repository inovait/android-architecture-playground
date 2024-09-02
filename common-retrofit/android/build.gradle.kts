plugins {
   androidLibraryModule
   di
}

dependencies {
   api(projects.common)
   api(projects.commonRetrofit)
   api(libs.dispatch)
   api(libs.kotlinova.core)
   api(libs.moshi)
   api(libs.okhttp)
   api(libs.kotlinova.retrofit)
   api(libs.certificateTransparency)

   implementation(libs.kotlin.coroutines)
   implementation(libs.certificateTransparency.android)
}
