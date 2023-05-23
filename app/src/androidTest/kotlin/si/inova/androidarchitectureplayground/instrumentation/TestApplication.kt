package si.inova.androidarchitectureplayground.instrumentation

import com.deliveryhero.whetstone.app.ContributesAppInjector
import si.inova.androidarchitectureplayground.MyApplication
import java.time.Duration

@ContributesAppInjector(generateAppComponent = false)
class TestApplication : MyApplication() {
   override val applicationComponent: TestAppComponent by lazy {
      DaggerTestAppComponent.factory().create(this)
   }

   init {
      // We need to make this duration call as a workaround for the https://issuetracker.google.com/issues/277672742
      Duration.ofMillis(0L)
   }
}
