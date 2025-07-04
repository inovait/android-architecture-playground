package jacoco

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.register
import org.gradle.testing.jacoco.tasks.JacocoReport

fun Project.registerJacocoConfigurations() {
   configurations.register(CONFIGURATION_JACOCO_SOURCES)
   configurations.register(CONFIGURATION_JACOCO_CLASSES)
   configurations.register(CONFIGURATION_JACOCO_EXEC)
}

abstract class JacocoClassGetterTask: DefaultTask() {
   @get:InputFiles
   abstract val allDirectories: ListProperty<Directory>

   @get:InputFiles
   abstract val allJars: ListProperty<RegularFile>

   @TaskAction
   fun action() {
      println("AD: ${allDirectories.get()}")
      println("allJars: ${allJars.get()}")
   }
}

fun Project.setupJacocoAndroid() {
   registerJacocoConfigurations()

   val classGetterTask = tasks.register<JacocoClassGetterTask>("getJacocoClasses")

   artifacts {
      add(CONFIGURATION_JACOCO_SOURCES, layout.projectDirectory.dir("src/main/kotlin"))

      // add(CONFIGURATION_JACOCO_CLASSES, classGetterTask.flatMap { it.allDirectories.map { it.map { it.asFile } } }) {
      //    builtBy(classGetterTask)
      // }
      add(CONFIGURATION_JACOCO_CLASSES, layout.buildDirectory.dir("tmp/kotlin-classes/debug").map { it.asFile })

      add(CONFIGURATION_JACOCO_EXEC, layout.buildDirectory.dir("jacoco").map { it.asFile })
      add(CONFIGURATION_JACOCO_EXEC, layout.buildDirectory.dir("outputs/code_coverage").map { it.asFile })
   }

   (extensions.getByName<AndroidComponentsExtension<*, *, *>>("androidComponents")).onVariants {variant ->
           variant.artifacts.forScope(ScopedArtifacts.Scope.PROJECT)
            .use(classGetterTask)
            .toGet(
                ScopedArtifact.CLASSES,
                JacocoClassGetterTask::allJars,
                JacocoClassGetterTask::allDirectories
            )
   }
}

@Suppress("UnstableApiUsage") // Isolated projects
fun Project.setupJacocoRoot() {
   registerJacocoConfigurations()

   tasks.register("aggregatedJacocoReport", JacocoReport::class).configure {
         classDirectories.from(configurations.getByName(CONFIGURATION_JACOCO_CLASSES).incoming.artifactView { isLenient = true }.files.map { it }.also { println("clases: ${it}") })
      sourceDirectories.from(configurations.getByName(CONFIGURATION_JACOCO_SOURCES).incoming.artifactView { isLenient = true }.files.map { it }.also { println("source: ${it}") })
      executionData.from(configurations.getByName(CONFIGURATION_JACOCO_EXEC).incoming.artifactView { isLenient = true }.files.map { it }.also { println("exec: ${it}") })
   }

   subprojects {
      this@setupJacocoRoot.dependencies.add(
         CONFIGURATION_JACOCO_CLASSES,
         this@setupJacocoRoot.dependencies.project(
            mapOf(
               "path" to isolated.path,
               "configuration" to CONFIGURATION_JACOCO_CLASSES
            )
         )
      )

      this@setupJacocoRoot.dependencies.add(
         CONFIGURATION_JACOCO_SOURCES,
         this@setupJacocoRoot.dependencies.project(
            mapOf(
               "path" to isolated.path,
               "configuration" to CONFIGURATION_JACOCO_SOURCES
            )
         )
      )

      this@setupJacocoRoot.dependencies.add(
         CONFIGURATION_JACOCO_EXEC,
         this@setupJacocoRoot.dependencies.project(
            mapOf(
               "path" to isolated.path,
               "configuration" to CONFIGURATION_JACOCO_EXEC
            )
         )
      )
   }
}

const val CONFIGURATION_JACOCO_SOURCES = "jacocoSources"
const val CONFIGURATION_JACOCO_CLASSES = "jacocoClasses"
const val CONFIGURATION_JACOCO_EXEC = "jacocoExec"
