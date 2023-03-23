plugins {
   pureKotlinModule
}

dependencies {
   api(projects.common)
   implementation(libs.kotlin.coroutines.test)
   implementation(libs.kotlin.coroutines)
   implementation(libs.dispatch)
   implementation(libs.kotest.assertions)
   implementation(libs.turbine)
}
