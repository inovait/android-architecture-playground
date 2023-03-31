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
   implementation(libs.whetstone.runtime.get().toString()) {
      // Workaround for the https://github.com/deliveryhero/whetstone/pull/81
      exclude(module = "appcompat")
   }
   anvil(libs.whetstone.compiler)
}
