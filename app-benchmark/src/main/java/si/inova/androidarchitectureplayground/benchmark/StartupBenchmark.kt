package si.inova.androidarchitectureplayground.benchmark

import android.content.ComponentName
import android.content.Intent
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import org.junit.Rule
import org.junit.Test

/**
 * This is an example startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 */
class StartupBenchmark {
   @get:Rule
   val benchmarkRule = MacrobenchmarkRule()

   @OptIn(ExperimentalMetricApi::class)
   @Test
   fun startup() {
      benchmarkRule.measureRepeated(
         packageName = "si.inova.androidarchitectureplayground",
         // JIT compilation is the amount of JIT that our app needed. If this number gets high, we need to update baseline profile
         metrics = listOf(StartupTimingMetric(), TraceSectionMetric("JIT compiling %", TraceSectionMetric.Mode.Sum)),
         iterations = 5,
         startupMode = StartupMode.COLD,
      ) {
         val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            component = ComponentName(
               "si.inova.androidarchitectureplayground",
               "si.inova.androidarchitectureplayground.MainActivity"
            )

            putExtra("BENCHMARK_AUTO_LOGIN", true)
         }

         startActivityAndWait(intent)
      }
   }
}
