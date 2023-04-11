package si.inova.androidarchitectureplayground.screens.nested

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.zhuinden.simplestack.Backstack
import kotlinx.parcelize.Parcelize
import si.inova.kotlinova.navigation.di.MainNavigation
import si.inova.kotlinova.navigation.instructions.navigateTo
import si.inova.kotlinova.navigation.navigator.Navigator
import si.inova.kotlinova.navigation.screenkeys.NoArgsScreenKey
import si.inova.kotlinova.navigation.screens.Screen

class NestedScreenA(
   private val navigator: Navigator,
   @MainNavigation
   private val mainBackstack: Backstack
) : Screen<NestedScreenAKey>() {
   @Composable
   override fun Content(key: NestedScreenAKey) {
      Column() {
         Text("Nested screen A $navigator")

         Button(onClick = { navigator.navigateTo(NestedScreenBKey) }) {
            Text("Navigate to nested screen B")
         }

         Button(onClick = { mainBackstack.goBack() }) {
            Text("Go back globally")
         }
      }
   }
}

@Parcelize
object NestedScreenAKey : NoArgsScreenKey()
