plugins {
   pureKotlinModule

   navigationApi
   testFixtures
}

dependencies {
   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.navigation)

   testFixturesApi(libs.kotlin.coroutines)
}
