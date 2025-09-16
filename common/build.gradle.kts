plugins {
   pureKotlinModule
}

abstract class ProducingTask: DefaultTask() {
   @TaskAction
   fun run() {
      val computedValue = "Hello"
      println("computedValue: $computedValue")
   }
}

tasks.register<ProducingTask>("producer") {
}

dependencies {
   implementation(libs.kotlin.coroutines)
   implementation(libs.dispatch)
   api(libs.kotlinova.core)

   testImplementation(projects.common.test)
   testImplementation(libs.kotlinova.core.test)
   testImplementation(libs.kotlinova.retrofit.test)
   testImplementation(libs.turbine)
}
