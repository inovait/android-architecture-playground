package si.inova.androidarchitectureplayground

import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.ServiceBinder
import si.inova.androidarchitectureplayground.screens.ScopedService
import si.inova.androidarchitectureplayground.screens.ScreenKey
import si.inova.androidarchitectureplayground.simplestack.SingleScreenViewModel
import javax.inject.Provider

class MyScopedServices : ScopedServices {
   lateinit var scopedServicesFactories: Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Provider<ScopedService>>
   lateinit var scopedServicesKeys: Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Provider<List<Class<*>>>>

   override fun bindServices(serviceBinder: ServiceBinder) {
      val serviceKeys = scopedServicesKeys.getValue(serviceBinder.getKey<@JvmSuppressWildcards Class<*>>().javaClass).get()

      for (key in serviceKeys) {
         val service = scopedServicesFactories.getValue(key).get()
         if (service is SingleScreenViewModel<*>) {
            @Suppress("UNCHECKED_CAST")
            (service as SingleScreenViewModel<ScreenKey>).init(serviceBinder.getKey() as ScreenKey)
         }
         serviceBinder.addService(key.name, service)
      }
   }
}
