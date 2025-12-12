plugins {
   pureKotlinModule
   di
}

dependencies {
   api(projects.commonRetrofit)
   api(libs.kotlinova.retrofit.test)

   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
   implementation(libs.kotlinova.core.test)
   implementation(libs.moshi)
   implementation(libs.okhttp)
   implementation(libs.kotlin.coroutines.test)
}
