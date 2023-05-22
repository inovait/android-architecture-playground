plugins {
   pureKotlinModule
}

dependencies {
   api(projects.post.api)
   implementation(projects.common)

   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
}
