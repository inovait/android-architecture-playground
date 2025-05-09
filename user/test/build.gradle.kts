plugins {
   pureKotlinModule
}

dependencies {
   api(projects.user.api)
   api(libs.androidx.paging.common)

   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
}
