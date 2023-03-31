plugins {
   pureKotlinModule
}

dependencies {
   api(projects.common)
   api(libs.kotlinova.core.test)
   implementation(libs.kotlin.coroutines.test)
   implementation(libs.kotlin.coroutines)
   implementation(libs.dispatch)
   implementation(libs.kotest.assertions)
   implementation(libs.turbine)
}
