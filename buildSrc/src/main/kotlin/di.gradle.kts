import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
   id("com.google.devtools.ksp")
}

dependencies {
   add("implementation", libs.kotlinInject.runtime)
   add("implementation", libs.kotlinInject.anvil.annotations)
   add("implementation", libs.kotlinInject.anvil.runtime)

   ksp(libs.kotlinInject.compiler)
   ksp(libs.kotlinInject.anvil.compiler)
}
