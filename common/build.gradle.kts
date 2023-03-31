plugins {
   pureKotlinModule
}

dependencies {
   implementation(libs.kotlin.coroutines)
   api(libs.kotlinova.core)

   testImplementation(projects.common.test)
   testImplementation(libs.turbine)
}
