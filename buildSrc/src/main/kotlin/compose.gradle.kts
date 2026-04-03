import com.skydoves.compose.stability.gradle.StabilityAnalyzerExtension
import org.gradle.accessors.dm.LibrariesForLibs
import util.isAndroidProject

val libs = the<LibrariesForLibs>()

plugins {
   id("org.jetbrains.kotlin.plugin.compose")
}

val stableClassesFile = project.layout.settingsDirectory.file("config/global_compose_stable_classes.txt")
composeCompiler {
   stabilityConfigurationFiles.add(stableClassesFile)
}

// Due to the https://github.com/skydoves/compose-stability-analyzer/issues/107, only enable stability analyzer
// when isolated projects are disabled
val isolatedProjects = objects.newInstance<BuildFeaturesAccessors>().buildFeatures.isolatedProjects.active.get()
if (!isolatedProjects) {
   apply(plugin = "com.github.skydoves.compose.stability.analyzer")

   configure<StabilityAnalyzerExtension> {
      stabilityValidation {
         ignoreNonRegressiveChanges = true
         allowMissingBaseline = true
         quietCheck = true
         stabilityConfigurationFiles.add(stableClassesFile)
      }
   }

   plugins.withId("com.android.base") {
      afterEvaluate {
         tasks.named { it == "androidTestJar" }.configureEach {
            shouldRunAfter("compileReleaseKotlin")
         }
      }
   }
} else if (
   gradle.startParameter.taskNames.any { it.contains("stabilityCheck", ignoreCase = true) } ||
   gradle.startParameter.taskNames.any { it.contains("stabilityDump", ignoreCase = true) }
) {
   error("Stability analyzer is not supported with isolated projects enabled.")
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

open class BuildFeaturesAccessors @Inject constructor(val buildFeatures: BuildFeatures)
