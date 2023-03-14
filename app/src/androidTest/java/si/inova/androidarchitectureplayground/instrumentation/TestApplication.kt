package si.inova.androidarchitectureplayground.instrumentation

import si.inova.androidarchitectureplayground.MyApplication

class TestApplication : MyApplication() {
   override val applicationComponent: TestAppComponent by lazy {
      DaggerTestAppComponent.factory().create(this)
   }
}
