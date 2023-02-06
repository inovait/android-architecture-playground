plugins {
   id("java-library")
   id("org.jetbrains.kotlin.jvm")
   id("kotlin-kapt")
}

java {
   sourceCompatibility = JavaVersion.VERSION_1_8
   targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
   api(libs.anvil.api)
   implementation(libs.anvil.utils)
   implementation(libs.dagger.runtime)
   compileOnly(libs.autoService.annotations)
   kapt(libs.autoService.compiler)
}
