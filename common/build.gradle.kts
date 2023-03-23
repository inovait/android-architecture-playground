plugins {
   pureKotlinModule
}

dependencies {
   implementation(libs.kotlin.coroutines)
   implementation(libs.turbine)

   testImplementation(projects.common.test)
}
