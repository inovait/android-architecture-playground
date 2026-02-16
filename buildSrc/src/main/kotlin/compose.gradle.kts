import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
   id("org.jetbrains.kotlin.plugin.compose")
   id("com.github.skydoves.compose.stability.analyzer")
}

val stableClassesFile = project.layout.settingsDirectory.file("config/global_compose_stable_classes.txt")
composeCompiler {
   stabilityConfigurationFiles.add(stableClassesFile)
}

composeStabilityAnalyzer {
   // Most of the time stability checker is not needed, so disable it to not waste CPU cycles and reduce build times
   enabled =
      providers.gradleProperty("forceEnableStabilityAnalyzer").orElse("false").get().toBoolean() ||
      gradle.startParameter.taskNames.any {
         it.contains("stabilityCheck", ignoreCase = true) ||
            it.contains("stabilityDump", ignoreCase = true)
      }

   stabilityValidation {
      ignoreNonRegressiveChanges = true
      allowMissingBaseline = true
      quietCheck = true
      stabilityConfigurationFiles.add(stableClassesFile)
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
