import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
   id("checks")
   id("com.squareup.anvil")
}

dependencies {
   add("implementation", libs.dagger.runtime)
   add("anvil", project(":anvil"))
}
