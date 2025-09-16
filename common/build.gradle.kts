plugins {
   pureKotlinModule
}

val customTasksConfiguration = project.configurations.consumable("customTasksElements") {
   attributes {
      attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, "custom_task"))
   }
}

abstract class ProducingTask : DefaultTask() {
   @get:OutputFile
   abstract val output: RegularFileProperty

   @TaskAction
   fun run() {
      val computedValue = "Hello"
      output.get().asFile.writeText(computedValue)
      println("computedValue: $computedValue")
   }
}

val producer = tasks.register<ProducingTask>("producer") {
   output = project.layout.buildDirectory.file("output.txt")
}

customTasksConfiguration.configure {
   outgoing.artifact(producer.map { it.output })
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
