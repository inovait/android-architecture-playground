plugins {
   pureKotlinModule
   di
}

dependencies {
   api(projects.user.api)
   implementation(projects.common)

   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
}
