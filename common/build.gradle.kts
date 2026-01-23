plugins {
   pureKotlinModule
   testFixtures
}

dependencies {
   implementation(libs.kotlin.coroutines)
   implementation(libs.dispatch)
   api(libs.kotlinova.core)

   testImplementation(testFixtures(projects.common))
   testImplementation(libs.kotlinova.core.test)
   testImplementation(libs.kotlinova.retrofit.test)
   testImplementation(libs.turbine)

   testFixturesImplementation(libs.kotlin.coroutines)
   testFixturesImplementation(libs.dispatch.test)
   testFixturesImplementation(libs.kotest.assertions)
   testFixturesImplementation(libs.turbine)
   testFixturesImplementation(libs.androidx.datastore.preferences.core)

}
