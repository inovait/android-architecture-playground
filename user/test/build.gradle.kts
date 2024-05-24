plugins {
   pureKotlinModule
}

dependencies {
   api(projects.common)
   api(projects.user.api)

   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
}
