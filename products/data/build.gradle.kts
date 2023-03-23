plugins {
   pureKotlinModule
   moshi
}

dependencies {
   implementation(projects.common)
   implementation(projects.commonRetrofit)

   implementation(projects.common.test)
   implementation(libs.kotlin.coroutines)

   testImplementation(projects.commonRetrofit.test)
}
