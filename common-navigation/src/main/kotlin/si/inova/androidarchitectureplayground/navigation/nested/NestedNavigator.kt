package si.inova.androidarchitectureplayground.navigation.nested

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zhuinden.simplestack.AsyncStateChanger
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import si.inova.androidarchitectureplayground.di.MainNavigation
import si.inova.androidarchitectureplayground.di.NavigationInjection
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey
import si.inova.androidarchitectureplayground.simplestack.BackstackProvider
import si.inova.androidarchitectureplayground.simplestack.ComposeStateChanger
import si.inova.androidarchitectureplayground.simplestack.rememberBackstack
import javax.inject.Inject

class NestedNavigator @Inject constructor(
   private val navigationStackComponentFactory: NavigationInjection.Factory,
   @MainNavigation
   private val mainBackstack: Backstack
) {
   @Composable
   fun NestedNavigation(id: String = "SINGLE", initialHistory: () -> History<ScreenKey>) {
      val backstack = navigationStackComponentFactory.rememberBackstack(
         id = id,
         initialHistory = initialHistory,
         overrideMainBackstack = mainBackstack
      )

      val stateChanger = ComposeStateChanger()
      remember(stateChanger) {
         backstack.setStateChanger(AsyncStateChanger(stateChanger))
         true
      }

      BackstackProvider(backstack) {
         stateChanger.Content()
      }
   }
}
