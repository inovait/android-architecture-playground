plugins {
   pureKotlinModule
   testHelpers
}

dependencies {
   api(projects.login.api)
   api(libs.kotlin.coroutines)
}
