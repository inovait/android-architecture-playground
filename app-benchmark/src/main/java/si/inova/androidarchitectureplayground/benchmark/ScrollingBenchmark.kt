package si.inova.androidarchitectureplayground.benchmark

import android.content.ComponentName
import android.content.Intent
import android.util.TypedValue
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.Rule
import org.junit.Test
import kotlin.math.roundToInt

/**
 * This is an example scrolling speed benchmark.
 *
 * It launches the default activity and scroll a bit.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 */
class ScrollingBenchmark {
   @get:Rule
   val benchmarkRule = MacrobenchmarkRule()

   @OptIn(ExperimentalMetricApi::class)
   @Test
   fun scrollUpAndDown() {
      benchmarkRule.measureRepeated(
         packageName = "si.inova.androidarchitectureplayground",
         // JIT compilation is the amount of JIT that our app needed. If this number gets high, we need to update baseline profile
         metrics = listOf(FrameTimingMetric(), TraceSectionMetric("JIT compiling %", TraceSectionMetric.Mode.Sum)),
         iterations = 5,
         startupMode = StartupMode.WARM,
         setupBlock = {
            val intent = Intent(Intent.ACTION_MAIN).apply {
               addCategory(Intent.CATEGORY_HOME)
               component = ComponentName(
                  "si.inova.androidarchitectureplayground",
                  "si.inova.androidarchitectureplayground.MainActivity"
               )

               putExtra("BENCHMARK_AUTO_LOGIN", true)
            }

            pressHome()
            startActivityAndWait(intent)
            device.wait(Until.hasObject(By.text("They rushed out the door.")), 5_000).shouldNotBeNull()
         }
      ) {
         val scrollOffset = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            500f,
            InstrumentationRegistry.getInstrumentation().context.resources.displayMetrics
         ).roundToInt()

         device.swipe(
            /* startX = */ device.displayWidth / 2,
            /* startY = */ device.displayHeight / 2 + scrollOffset / 2,
            /* endX = */ device.displayWidth / 2,
            /* endY = */ device.displayHeight / 2 - scrollOffset / 2,
            /* steps = */ 10
         )
         Thread.sleep(500)

         device.swipe(
            /* startX = */ device.displayWidth / 2,
            /* startY = */ device.displayHeight / 2 - scrollOffset / 2,
            /* endX = */ device.displayWidth / 2,
            /* endY = */ device.displayHeight / 2 + scrollOffset / 2,
            /* steps = */ 10
         )
         Thread.sleep(500)
      }
   }
}
