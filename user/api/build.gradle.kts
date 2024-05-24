plugins {
   pureKotlinModule
}

dependencies {
   api(projects.common)

   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
}
