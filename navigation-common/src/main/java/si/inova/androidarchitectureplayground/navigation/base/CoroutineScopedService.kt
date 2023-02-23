package si.inova.androidarchitectureplayground.navigation.base

import androidx.annotation.CallSuper
import com.zhuinden.simplestack.ScopedServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

abstract class CoroutineScopedService(context: CoroutineContext = Dispatchers.Main.immediate) : ScopedService,
   ScopedServices.Registered {
   @Suppress("MemberVisibilityCanBePrivate")
   val coroutineScope = CoroutineScope(SupervisorJob() + context)

   override fun onServiceRegistered() {}

   @CallSuper
   override fun onServiceUnregistered() {
      coroutineScope.cancel()
   }
}
