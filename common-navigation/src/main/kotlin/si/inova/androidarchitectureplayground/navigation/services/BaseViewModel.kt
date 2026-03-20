package si.inova.androidarchitectureplayground.navigation.services

import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.navigation.screenkeys.ScreenKey
import si.inova.kotlinova.navigation.services.SingleScreenViewModel

abstract class BaseViewModel<K : ScreenKey>(
   coroutineResourceManagerFactory: CoroutineResourceManager.Factory,
   protected val resources: CoroutineResourceManager = coroutineResourceManagerFactory.create(""),
) : SingleScreenViewModel<K>(resources.scope) {
   init {
      resources.tag = this.javaClass.simpleName
   }
}
