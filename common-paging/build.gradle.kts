plugins {
   pureKotlinModule
}

dependencies {
   implementation(libs.androidx.paging.common)
   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)

   testImplementation(libs.androidx.paging.testing)
   testImplementation(libs.kotlinova.core.test)
   testImplementation(libs.kotlinova.retrofit.test)
}
