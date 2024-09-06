import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import util.commonAndroid

val libs = the<LibrariesForLibs>()

plugins {
   id("com.joetr.compose.guard")
}

commonAndroid {
   buildFeatures {
      compose = true
   }
   composeOptions {
      kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
   }
}

//region Compose Guard
composeGuardCheck {
   // Dynamic property detection is error prone on debug builds (and we are only building debug builds for PR changes)
   // See https://chrisbanes.me/posts/composable-metrics/#default-parameter-expressions-that-are-dynamic
   errorOnNewDynamicProperties = false

   // We don't care about unstable classes, only unstable Composables
   errorOnNewUnstableClasses = false
}

// Workaround for https://github.com/j-roskopf/ComposeGuard/issues/47 - manually register compose reports and metrics folder
composeGuard {
   configureKotlinTasks = false
}

// List of all tasks in this module that compile compose stuff (excluding KSP etc.)
val composeCompileTasks = listOf("compileDebugKotlin", "compileReleaseKotlin")

val composeReportsFolder = composeGuardCheck.outputDirectory.get()
project.tasks.named { composeCompileTasks.contains(it) }.withType<KotlinCompile<*>>().configureEach {
   kotlinOptions {
      freeCompilerArgs += listOf(
         "-P",
         "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
            "$composeReportsFolder"
      )
      freeCompilerArgs += listOf(
         "-P",
         "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
            "$composeReportsFolder"
      )
   }

   outputs.dir(composeReportsFolder)
}

afterEvaluate {
   val kotlinTasks = ArrayList<String>()
   project.tasks.withType<KotlinCompile<*>>() {
      kotlinTasks += name
   }
   project.tasks.named("debugComposeCompilerCheck") {
      // Compose guard accesses outputs of all kotlin tasks, but does not properly declare its dependencies
      // We fix that for them
      for (task in kotlinTasks) {
         mustRunAfter(task)
      }
   }
}

tasks.register<Copy>("generateComposeGuardBaseline") {
   from(composeReportsFolder)
   into(composeGuardGenerate.outputDirectory)

   dependsOn("compileDebugKotlin")
}
//endregion

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
