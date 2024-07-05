import org.gradle.accessors.dm.LibrariesForLibs
import util.commonAndroid
import util.commonKotlinOptions

val libs = the<LibrariesForLibs>()

commonAndroid {
   buildFeatures {
      compose = true
   }
   composeOptions {
      kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
   }
   commonKotlinOptions {
      freeCompilerArgs += listOf(
         "-P",
         "plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=" +
            "${rootDir.absolutePath}/config/global_compose_stable_classes.txt"
      )
   }
}

dependencies {
   add("implementation", libs.androidx.compose.ui)
   add("implementation", libs.androidx.compose.ui.graphics)
   add("implementation", libs.androidx.compose.ui.tooling.preview)
   add("implementation", libs.androidx.compose.material3)
   add("implementation", libs.androidx.lifecycle.compose)
   add("implementation", libs.kotlinova.compose)

   add("debugRuntimeOnly", libs.androidx.compose.ui.test.manifest)
   add("debugImplementation", libs.androidx.compose.ui.tooling)
   add("debugImplementation", libs.rebugger)

   add("androidTestImplementation", libs.androidx.compose.ui.test.junit4)
}
