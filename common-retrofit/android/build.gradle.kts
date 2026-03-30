plugins {
   androidLibraryModule
   di
}

dependencies {
   api(projects.commonRetrofit)
   api(libs.dispatch)
   api(libs.kotlinova.core)
   api(libs.kotlinova.retrofit)
   api(libs.okhttp)
   api(libs.kotlin.serialization.json)

   implementation(libs.kotlin.coroutines)
}
