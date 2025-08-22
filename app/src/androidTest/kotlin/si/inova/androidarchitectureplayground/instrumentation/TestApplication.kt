package si.inova.androidarchitectureplayground.instrumentation

import dev.zacsweers.metro.createGraphFactory
import si.inova.androidarchitectureplayground.MyApplication
import si.inova.androidarchitectureplayground.di.ApplicationGraph

class TestApplication : MyApplication() {
   override val applicationGraph: ApplicationGraph by lazy {
      createGraphFactory<TestApplicationGraph.Factory>().create(this)
   }
}
