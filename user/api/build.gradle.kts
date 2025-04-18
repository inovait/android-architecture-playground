plugins {
   pureKotlinModule
}

dependencies {
   api(projects.common)

   implementation(libs.androidx.paging.common)
   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
   compileOnly(libs.androidx.compose.runtime)
}
