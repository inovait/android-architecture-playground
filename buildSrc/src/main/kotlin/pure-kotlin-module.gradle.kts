import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
   id("org.jetbrains.kotlin.jvm")

   id("all-modules-commons")
}

dependencies {
}
