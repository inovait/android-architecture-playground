package si.inova.androidarchitectureplayground.screens.nested

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.parcelize.Parcelize
import si.inova.kotlinova.navigation.instructions.goBack
import si.inova.kotlinova.navigation.navigator.Navigator
import si.inova.kotlinova.navigation.screenkeys.NoArgsScreenKey
import si.inova.kotlinova.navigation.screens.Screen

class NestedScreenB(
   private val navigator: Navigator,
) : Screen<NestedScreenBKey>() {
   @Composable
   override fun Content(key: NestedScreenBKey) {
      Column() {
         Text("Nested screen B")

         Button(onClick = { navigator.goBack() }) {
            Text("Go back")
         }
      }
   }
}

@Parcelize
object NestedScreenBKey : NoArgsScreenKey()
