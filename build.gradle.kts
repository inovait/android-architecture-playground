import com.android.build.gradle.internal.lint.AndroidLintTask
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.report.ReportMergeTask

val reportMerge by tasks.registering(ReportMergeTask::class) {
   output.set(File(rootProject.rootDir, "merge.sarif"))

   doFirst {
      output.get().asFile.delete()
   }
}

// This violates build isolation but we are forced to use it for performance reasons
// Waiting for https://github.com/gradle/gradle/issues/25179 for a possible workaround
// Please do not add anything else to the subprojects {}
val inputs = ArrayList<Provider<RegularFile>>()

subprojects {
   val configuredDetektTasks = ArrayList<Detekt>()
   val configuredLintTasks = ArrayList<AndroidLintTask>()

   // Hack: Use configureEach to get list of currently executing tasks, to ensure we do not

   tasks.withType<Detekt>().configureEach { configuredDetektTasks.add(this) }
   tasks.withType<AndroidLintTask>().configureEach { configuredLintTasks.add(this) }

   reportMerge {
      input.from("buildSrc/build/reports/detekt/detekt.sarif")

      input.from(
         provider { configuredDetektTasks }.flatMap { list ->
            provider {
               list.map {
                  it.sarifReportFile.get()
               }
            }
         }
      )

      input.from(
         provider { configuredLintTasks }.flatMap { list ->
            provider {
               list.map { lintTask ->
                  val variant = lintTask.variantInputs.name.get()
                  lintTask.project.layout.buildDirectory.file(
                     "reports/lint-results-$variant.sarif"
                  ).get()
               }
            }
         }
      )
   }
}
