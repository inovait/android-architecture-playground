plugins {
   pureKotlinModule
}

dependencies {
   api(projects.commonRetrofit)
   api(libs.okhttp.mockWebServer)

   implementation(projects.common.test)
   implementation(libs.kotlin.coroutines.test)
}
