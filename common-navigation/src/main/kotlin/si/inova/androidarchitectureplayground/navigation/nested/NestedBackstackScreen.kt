package si.inova.androidarchitectureplayground.navigation.nested

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zhuinden.simplestack.AsyncStateChanger
import com.zhuinden.simplestack.Backstack
import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.di.MainNavigation
import si.inova.androidarchitectureplayground.di.NavigationInjection
import si.inova.androidarchitectureplayground.navigation.base.Screen
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey
import si.inova.androidarchitectureplayground.simplestack.BackstackProvider
import si.inova.androidarchitectureplayground.simplestack.ComposeStateChanger
import si.inova.androidarchitectureplayground.simplestack.rememberBackstack

class NestedBackstackScreen(
   private val navigationStackComponentFactory: NavigationInjection.Factory,
   @MainNavigation
   private val mainBackstack: Backstack
) : Screen<NestedNavigationScreenKey>() {
   @Composable
   override fun Content(key: NestedNavigationScreenKey) {
      val backstack = navigationStackComponentFactory.rememberBackstack(
         id = key.id,
         initialHistory = { key.initialHistory },
         interceptBackButton = key.interceptBackButton,
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

@Parcelize
data class NestedNavigationScreenKey(
   val initialHistory: List<ScreenKey>,
   val id: String = "SINGLE",
   val interceptBackButton: Boolean = false
) : ScreenKey()
