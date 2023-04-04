package si.inova.androidarchitectureplayground.instrumentation

import com.deliveryhero.whetstone.app.ContributesAppInjector
import si.inova.androidarchitectureplayground.MyApplication
import java.time.Duration

@ContributesAppInjector(generateAppComponent = false)
class TestApplication : MyApplication() {
   override val applicationComponent: TestAppComponent by lazy {
      // We need to make this duration call as a workaround for the https://issuetracker.google.com/issues/274626810
      Duration.ofMillis(0L)

      DaggerTestAppComponent.factory().create(this)
   }
}
