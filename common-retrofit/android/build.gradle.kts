plugins {
   androidLibraryModule
   di
}

dependencies {
   api(projects.commonRetrofit)
   api(libs.dispatch)
   api(libs.kotlinova.core)
   api(libs.kotlinova.retrofit)
   api(libs.moshi)
   api(libs.okhttp)

   implementation(libs.kotlin.coroutines)
}
