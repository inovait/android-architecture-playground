import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

val libs = the<LibrariesForLibs>()

plugins {
   id("org.jetbrains.kotlin.jvm")

   id("all-modules-commons")
}

tasks.withType(KotlinCompilationTask::class.java) {
   compilerOptions.freeCompilerArgs.add("-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
   compilerOptions.freeCompilerArgs.add("-Xopt-in=kotlinx.coroutines.FlowPreview")
}

tasks.test {
   useJUnitPlatform()
}

dependencies {
}
