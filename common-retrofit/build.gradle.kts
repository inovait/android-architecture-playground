plugins {
   pureKotlinModule
}

dependencies {
   api(libs.okhttp)
   api(libs.moshi)
   api(libs.retrofit)

   implementation(libs.dispatch)
   implementation(libs.retrofit.moshi)
   implementation(libs.kotlin.coroutines)

   implementation(projects.common)

   testImplementation(projects.common.test)
   testImplementation(projects.commonRetrofit.test)
   testImplementation(libs.turbine)
}
