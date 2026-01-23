plugins {
   pureKotlinModule
   testFixtures
}

dependencies {
   implementation(libs.androidx.paging.common)
   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
   compileOnly(libs.androidx.compose.runtime)

   testFixturesApi(libs.androidx.paging.common)

   testFixturesImplementation(libs.kotlin.coroutines)
   testFixturesImplementation(libs.kotlinova.core)

}
