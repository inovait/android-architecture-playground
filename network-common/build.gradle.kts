plugins {
   pureKotlinModule
}

dependencies {
   api(libs.okhttp)
   api(libs.moshi)
   api(libs.retrofit)

   implementation(libs.retrofit.moshi)

   implementation(projects.common.pure)
}
