plugins {
   pureKotlinModule
   moshi
}

dependencies {
   implementation(projects.common)
   implementation(projects.commonRetrofit)

   implementation(libs.kotlin.coroutines)

   testImplementation(projects.common.test)
   testImplementation(projects.commonRetrofit.test)
}
