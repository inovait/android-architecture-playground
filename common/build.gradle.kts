plugins {
   pureKotlinModule
}

dependencies {
   implementation(libs.kotlin.coroutines)

   testImplementation(projects.common.test)
   testImplementation(libs.turbine)
}
