package si.inova.androidarchitectureplayground.instrumentation

import si.inova.androidarchitectureplayground.MyApplication
import si.inova.androidarchitectureplayground.di.ApplicationGraph

class TestApplication : MyApplication() {
   override val applicationGraph: ApplicationGraph by lazy {
      TestApplicationGraph::class.create(this)
   }
}
