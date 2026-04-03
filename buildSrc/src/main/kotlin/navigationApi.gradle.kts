import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
   id("serialization")

   // Run compose plugin to get @StabilityInferred on all data classes in the API folder
   id("org.jetbrains.kotlin.plugin.compose")
}

dependencies {
   if (name != "common-navigation") {
      add("implementation", project(":common-navigation"))
   }

   add("compileOnly", libs.androidx.compose.runtime)

   plugins.withId("test-fixtures") {
      add("testFixturesCompileOnly", libs.androidx.compose.runtime)
   }
}
