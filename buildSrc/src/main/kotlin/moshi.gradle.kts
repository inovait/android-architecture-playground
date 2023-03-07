import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.the

val libs = the<LibrariesForLibs>()

plugins {
   id("com.google.devtools.ksp")
}

dependencies {
   add("implementation", libs.moshi)
   add("ksp", libs.moshi.codegen)
}
