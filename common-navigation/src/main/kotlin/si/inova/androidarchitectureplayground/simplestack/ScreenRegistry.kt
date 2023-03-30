package si.inova.androidarchitectureplayground.simplestack

import si.inova.androidarchitectureplayground.navigation.base.Screen
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey
import javax.inject.Inject
import javax.inject.Provider

class ScreenRegistry @Inject constructor(
   private val staticRegistrations: Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards ScreenRegistration<*>>,
   private val screenFactories: Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Provider<Screen<*>>>,
) {
   @Suppress("UNCHECKED_CAST")
   fun <T : ScreenKey> createScreen(key: T): Screen<T> {
      val registration = getRegistration(key)

      val factory = screenFactories[registration.screenClass]
         ?: error("Missing screen factory for the ${registration.screenClass.name}")

      return factory.get() as Screen<T>
   }

   @Suppress("UNCHECKED_CAST")
   fun <T : ScreenKey> getRegistration(key: T): ScreenRegistration<T> {
      val reg = staticRegistrations[key.javaClass]
         ?: error("No screen registered for ${key.javaClass.name}")

      return reg as ScreenRegistration<T>
   }
}
