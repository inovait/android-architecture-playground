package si.inova.androidarchitectureplayground.simplestack

import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.ServiceBinder
import si.inova.androidarchitectureplayground.navigation.base.ScopedService
import si.inova.androidarchitectureplayground.navigation.base.SingleScreenViewModel
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey
import javax.inject.Provider

class MyScopedServices : ScopedServices {
   lateinit var scopedServicesFactories: Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Provider<ScopedService>>
   lateinit var screenRegistry: ScreenRegistry

   override fun bindServices(serviceBinder: ServiceBinder) {
      val serviceKeys = screenRegistry.getRegistration(serviceBinder.getKey()).serviceClasses

      for (key in serviceKeys) {
         val service = scopedServicesFactories.getValue(key).get()
         if (service is SingleScreenViewModel<*>) {
            @Suppress("UNCHECKED_CAST")
            (service as SingleScreenViewModel<ScreenKey>).key = serviceBinder.getKey() as ScreenKey
         }
         serviceBinder.addService(key.name, service)
      }
   }
}
