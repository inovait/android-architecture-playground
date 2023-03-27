package si.inova.androidarchitectureplayground

import android.app.Application
import com.deliveryhero.whetstone.app.ApplicationComponent
import com.deliveryhero.whetstone.app.ApplicationComponentOwner
import com.deliveryhero.whetstone.app.ContributesAppInjector
import si.inova.androidarchitectureplayground.common.logging.LogPriority
import si.inova.androidarchitectureplayground.di.DaggerMyApplicationComponent
import si.inova.androidarchitectureplayground.logging.AndroidLogcatLogger

@ContributesAppInjector(generateAppComponent = false)
open class MyApplication : Application(), ApplicationComponentOwner {
   override fun onCreate() {
      super.onCreate()

      AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)
   }

   override val applicationComponent: ApplicationComponent by lazy {
      DaggerMyApplicationComponent.factory().create(this)
   }
}
