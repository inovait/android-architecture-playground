package si.inova.androidarchitectureplayground

import android.app.Application
import com.deliveryhero.whetstone.app.ApplicationComponentOwner
import com.deliveryhero.whetstone.app.ContributesAppInjector
import si.inova.androidarchitectureplayground.di.DaggerMyApplicationComponent

@ContributesAppInjector(generateAppComponent = false)
class MyApplication : Application(), ApplicationComponentOwner {

   override val applicationComponent by lazy {
      DaggerMyApplicationComponent.factory().create(this)
   }
}
