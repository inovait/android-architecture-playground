plugins {
   pureKotlinModule
   testFixtures
}

dependencies {
   api(projects.common)

   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
   compileOnly(libs.androidx.compose.runtime)

   testFixturesApi(projects.common)
   testFixturesImplementation(libs.kotlin.coroutines)
}
