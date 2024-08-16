package si.inova.androidarchitectureplayground.instrumentation

import si.inova.androidarchitectureplayground.MyApplication
import si.inova.androidarchitectureplayground.di.ApplicationComponent

class TestApplication : MyApplication() {
   override val applicationComponent: ApplicationComponent by lazy {
      DaggerTestAppComponent.factory().create(this)
   }
}
