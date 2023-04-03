import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.the

val libs = the<LibrariesForLibs>()

plugins {
   id("dev.zacsweers.moshix")
}

dependencies {
   add("implementation", libs.moshi)
}
