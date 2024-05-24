plugins {
   pureKotlinModule
}

dependencies {
   api(projects.post.api)
   api(projects.common)

   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
}
