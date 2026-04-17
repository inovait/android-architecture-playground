plugins {
   pureKotlinModule

   navigationApi
   testFixtures
}

dependencies {
   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.navigation)

   testFixturesImplementation(libs.kotlin.coroutines)
}
