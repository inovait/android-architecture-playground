plugins {
   pureKotlinModule
   testFixtures
}

dependencies {
   implementation(libs.kotlin.coroutines)

   testFixturesApi(libs.kotlin.coroutines)
}
