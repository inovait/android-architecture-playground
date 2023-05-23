package si.inova.androidarchitectureplayground.instrumentation

import si.inova.androidarchitectureplayground.MyApplication
import si.inova.androidarchitectureplayground.di.ApplicationComponent
import java.time.Duration

class TestApplication : MyApplication() {
   override val applicationComponent: ApplicationComponent by lazy {
      DaggerTestAppComponent.factory().create(this)
   }

   init {
      // We need to make this duration call as a workaround for the https://issuetracker.google.com/issues/277672742
      Duration.ofMillis(0L)
   }
}
