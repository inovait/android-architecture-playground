import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
   id("checks")
   id("com.squareup.anvil")
}

dependencies {
   add("implementation", libs.dagger.runtime)
   add("anvil", libs.kotlinova.navigation.compiler)

   add("testImplementation", libs.junit5.api)
   add("testImplementation", libs.kotest.assertions)
   add("testImplementation", libs.kotlin.coroutines.test)
   add("testRuntimeOnly", libs.junit5.engine)
}
