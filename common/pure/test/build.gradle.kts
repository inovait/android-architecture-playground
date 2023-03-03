plugins {
   pureKotlinModule
}

dependencies {
   implementation(libs.kotlin.coroutines)

   api(projects.common.pure)
}
