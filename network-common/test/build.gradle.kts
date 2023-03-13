plugins {
   pureKotlinModule
}

dependencies {
   api(projects.networkCommon)
   api(libs.okhttp.mockWebServer)

   implementation(projects.common.pure.test)
   implementation(libs.kotlin.coroutines.test)
}
