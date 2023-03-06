package si.inova.androidarchitectureplayground.navigation.base

import androidx.annotation.CallSuper
import com.zhuinden.simplestack.ScopedServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

/**
 * Scoped service with a coroutine scope.
 *
 * Scope is provided via constructor, allowing mocks with tests. Provided scope will get cancelled when this scoped service
 * gets unloaded.
 */
abstract class CoroutineScopedService(
   @Suppress("MemberVisibilityCanBePrivate")
   val coroutineScope: CoroutineScope
) : ScopedService,
   ScopedServices.Registered {

   override fun onServiceRegistered() {}

   @CallSuper
   override fun onServiceUnregistered() {
      coroutineScope.cancel()
   }
}
