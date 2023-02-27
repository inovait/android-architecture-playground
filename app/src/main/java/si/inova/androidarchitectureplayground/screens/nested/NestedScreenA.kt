package si.inova.androidarchitectureplayground.screens.nested

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.zhuinden.simplestack.Backstack
import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.di.MainNavigation
import si.inova.androidarchitectureplayground.navigation.Navigator
import si.inova.androidarchitectureplayground.navigation.base.Screen
import si.inova.androidarchitectureplayground.navigation.instructions.navigateTo
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

class NestedScreenA(
   private val navigator: Navigator,
   @MainNavigation
   private val mainBackstack: Backstack
) : Screen<NestedScreenAKey>() {
   @Composable
   override fun Content(key: NestedScreenAKey) {
      Column() {
         Text("Nested screen A")

         Button(onClick = { navigator.navigateTo(NestedScreenBKey()) }) {
            Text("Navigate to nested screen B")
         }

         Button(onClick = { mainBackstack.goBack() }) {
            Text("Go back globally")
         }
      }
   }
}

@Parcelize
class NestedScreenAKey : ScreenKey() {
   override val screenClass: String
      get() = "si.inova.androidarchitectureplayground.screens.nested.NestedScreenA"
}
