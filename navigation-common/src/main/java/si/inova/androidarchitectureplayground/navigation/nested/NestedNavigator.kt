package si.inova.androidarchitectureplayground.navigation.nested

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zhuinden.simplestack.AsyncStateChanger
import com.zhuinden.simplestack.History
import si.inova.androidarchitectureplayground.di.NavigationInjection
import si.inova.androidarchitectureplayground.navigation.base.Screen
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey
import si.inova.androidarchitectureplayground.simplestack.BackstackProvider
import si.inova.androidarchitectureplayground.simplestack.ComposeStateChanger
import si.inova.androidarchitectureplayground.simplestack.LocalBackstack
import si.inova.androidarchitectureplayground.simplestack.MyScopedServices
import si.inova.androidarchitectureplayground.simplestack.rememberBackstack
import javax.inject.Inject
import javax.inject.Provider

class NestedNavigator @Inject constructor(
   private val navigationStackComponentFactory: NavigationInjection.Factory
) {
   @Composable
   fun NestedNavigation(initialKeys: () -> History<ScreenKey>) {
      val parentBackstack = LocalBackstack.current

      var screenFactories: Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Provider<Screen<*>>>? = null

      val stateChanger = remember {
         ComposeStateChanger(screenFactories = lazy { requireNotNull(screenFactories) })
      }
      val asyncStateChanger = remember(stateChanger) { AsyncStateChanger(stateChanger) }
      val backstack = rememberBackstack(asyncStateChanger) {
         val scopedServices = MyScopedServices()
         val backstack = createBackstack(
            initialKeys = initialKeys(),
            scopedServices = scopedServices
         )

         val component = navigationStackComponentFactory.create(backstack, parentBackstack)

         screenFactories = component.screenFactories()
         scopedServices.scopedServicesFactories = component.scopedServicesFactories()
         scopedServices.scopedServicesKeys = component.scopedServicesKeys()

         backstack
      }

      if (screenFactories == null) {
         val component = navigationStackComponentFactory.create(backstack, parentBackstack)
         screenFactories = component.screenFactories()
      }

      BackstackProvider(backstack) {
         stateChanger.Content()
      }
   }
}