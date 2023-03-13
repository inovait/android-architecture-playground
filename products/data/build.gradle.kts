plugins {
   pureKotlinModule
   moshi
}

dependencies {
   implementation(projects.common.pure)
   implementation(projects.networkCommon)

   implementation(projects.common.pure.test)
   implementation(libs.kotlin.coroutines)

   testImplementation(projects.networkCommon.test)
}
