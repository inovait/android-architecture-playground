package si.inova.androidarchitectureplayground.instrumentation

import com.deliveryhero.whetstone.app.ContributesAppInjector
import si.inova.androidarchitectureplayground.MyApplication

@ContributesAppInjector(generateAppComponent = false)
class TestApplication : MyApplication() {
   override val applicationComponent: TestAppComponent by lazy {
      DaggerTestAppComponent.factory().create(this)
   }
}
