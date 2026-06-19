package si.inova.androidarchitectureplayground.instrumentation

import dev.zacsweers.metro.createGraphFactory
import si.inova.androidarchitectureplayground.MyApplication
import si.inova.androidarchitectureplayground.di.ApplicationGraph
import java.time.Duration
import java.util.TimeZone

class TestApplication : MyApplication() {
   override val applicationGraph: ApplicationGraph by lazy {
      createGraphFactory<TestApplicationGraph.Factory>().create(this)
   }

   init {
      // We need to make these calls as a workaround for the https://issuetracker.google.com/issues/277672742
      Duration.ofMillis(0L)
      Duration.ofSeconds(0L).toMillis()
      TimeZone.getTimeZone("UTC")
   }
}
