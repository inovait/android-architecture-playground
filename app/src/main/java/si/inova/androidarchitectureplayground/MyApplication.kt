package si.inova.androidarchitectureplayground

import android.app.Application
import com.deliveryhero.whetstone.app.ApplicationComponentOwner
import com.deliveryhero.whetstone.app.ContributesAppInjector

@ContributesAppInjector(generateAppComponent = true)
class MyApplication : Application(), ApplicationComponentOwner {
   override val applicationComponent by lazy {
      GeneratedApplicationComponent.create(this)
   }
}
