plugins {
   pureKotlinModule
   moshi
}

dependencies {
   implementation(projects.common.pure)
   implementation(projects.networkCommon)

   implementation(libs.kotlin.coroutines)
}
