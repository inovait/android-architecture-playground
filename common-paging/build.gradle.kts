plugins {
   pureKotlinModule
}

dependencies {
   implementation(libs.androidx.paging.common)
   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)

   testImplementation(libs.kotlinova.core.test)
}
