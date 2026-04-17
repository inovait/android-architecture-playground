plugins {
   pureKotlinModule
   navigationApi
   testFixtures
}

dependencies {
   implementation(projects.common)
   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
   compileOnly(libs.androidx.compose.runtime)

   testFixturesImplementation(projects.common)
   testFixturesImplementation(libs.kotlin.coroutines)
   testFixturesImplementation(libs.kotlinova.core)
}
