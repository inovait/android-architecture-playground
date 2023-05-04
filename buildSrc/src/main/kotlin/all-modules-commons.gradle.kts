import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
   id("checks")
   id("com.squareup.anvil")
}

dependencies {
   add("implementation", libs.dagger.runtime)

   add("testImplementation", libs.junit5.api)
   add("testImplementation", libs.kotest.assertions)
   add("testImplementation", libs.kotlin.coroutines.test)
   if (path != ":common:test") {
      add("testImplementation", libs.kotlinova.core.test)
   }

   add("testRuntimeOnly", libs.junit5.engine)
}
