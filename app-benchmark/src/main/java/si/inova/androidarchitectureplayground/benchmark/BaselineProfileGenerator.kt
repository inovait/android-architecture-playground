package si.inova.androidarchitectureplayground.benchmark

import android.content.ComponentName
import android.content.Intent
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@Ignore // Disable during normal benchmarks. Comment out when generating profiles
class BaselineProfileGenerator {
   @get:Rule
   val profileRule = BaselineProfileRule()

   @OptIn(ExperimentalMetricApi::class)
   @Test
   fun startup() {
      profileRule.collect(
         packageName = "si.inova.androidarchitectureplayground",
         includeInStartupProfile = true
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
