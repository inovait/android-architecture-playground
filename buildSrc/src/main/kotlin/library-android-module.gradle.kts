import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.dependencies

val libs = the<LibrariesForLibs>()

plugins {
   id("com.android.library")
   id("android-module-commons")
}

anvil {
   generateDaggerFactories.set(true)
}

dependencies {
   implementation(libs.whetstone.runtime)
   anvil(libs.whetstone.compiler)
}
