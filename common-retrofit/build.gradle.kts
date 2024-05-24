plugins {
   pureKotlinModule
   di
   moshi
}

dependencies {
   api(projects.common)
   api(libs.certificateTransparency)
   api(libs.dagger.runtime)
   api(libs.kotlinova.core)
   api(libs.kotlinova.retrofit)
   api(libs.moshi)
   api(libs.okhttp)
   api(libs.retrofit)

   implementation(libs.okio)
   implementation(libs.retrofit.moshi)
   implementation(libs.kotlin.coroutines)

   testImplementation(projects.commonRetrofit.test)
   testImplementation(libs.kotlinova.retrofit.test)
   testImplementation(libs.okhttp.mockWebServer)
   testImplementation(libs.turbine)
}
