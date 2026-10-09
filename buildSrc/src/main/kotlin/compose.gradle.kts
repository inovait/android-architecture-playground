import com.skydoves.compose.stability.gradle.StabilityAnalyzerExtension
import com.skydoves.compose.stability.gradle.StabilityDumpTask
import org.gradle.accessors.dm.LibrariesForLibs
import util.isAndroidProject

val libs = the<LibrariesForLibs>()

plugins {
   id("org.jetbrains.kotlin.plugin.compose")
   id("com.github.skydoves.compose.stability.analyzer")
}

val stableClassesFile = project.layout.settingsDirectory.file("config/global_compose_stable_classes.txt")
composeCompiler {
   stabilityConfigurationFiles.add(stableClassesFile)
}

configure<StabilityAnalyzerExtension> {
   stabilityValidation {
      enabled = false

      ignoreNonRegressiveChanges = true
      allowMissingBaseline = true
      quietCheck = true
      allowIncrementalDisabling = false
   }

   stabilityConfigurationFiles.add(stableClassesFile)
}

tasks.withType<StabilityDumpTask>().configureEach {
   val forceProperty = project.providers.gradleProperty("force.stability.dump")

   doFirst {
      if (forceProperty.orNull?.toBoolean() != true) {
         error(
            "You should not be using stabilityDump in most cases. " +
               "Instead, please try to make your Composables stable instead.\n\n" +
               "If a specific Composable cannot be made stable (for example, due to legacy code or generics), " +
               "mark it with @IgnoreStabilityReport instead.\n\n" +
               "If you are absolutely sure that the stabilityDump is the right move, " +
               "set the force.stability.dump property to true and try again."
         )
      }
   }
}

dependencies {
   add("implementation", libs.androidx.compose.ui)
   add("implementation", libs.androidx.compose.ui.graphics)
   add("implementation", libs.androidx.compose.ui.tooling.preview)
   add("implementation", libs.androidx.compose.material3)
   add("implementation", libs.androidx.lifecycle.compose)
   add("implementation", libs.kotlinova.compose)

   if (isAndroidProject()) {
      add("debugRuntimeOnly", libs.androidx.compose.ui.test.manifest)
      add("debugImplementation", libs.androidx.compose.ui.tooling)
      add("debugImplementation", libs.rebugger)

      add("androidTestImplementation", libs.androidx.compose.ui.test.junit4)
   }
}
