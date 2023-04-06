package si.inova.androidarchitectureplayground.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import si.inova.androidarchitectureplayground.navigation.keys.ScreenAKey
import si.inova.androidarchitectureplayground.navigation.keys.ScreenBKey
import si.inova.kotlinova.compose.result.registerResultReceiver
import si.inova.kotlinova.core.logging.logcat
import si.inova.kotlinova.navigation.instructions.navigateTo
import si.inova.kotlinova.navigation.navigator.Navigator
import si.inova.kotlinova.navigation.screens.Screen
import kotlin.random.Random

@Suppress("unused")
class ScreenA constructor(
   private val viewModel: ScreenAViewModel,
   private val navigator: Navigator,
) : Screen<ScreenAKey>() {
   @Composable
   override fun Content(key: ScreenAKey) {
      val rememberedNumber = rememberSaveable() { Random.nextInt() }

      val resultKey = registerResultReceiver<String> {
         logcat { "Received $it" }
      }

      Column(
         Modifier
            .fillMaxSize()
            .background(Color.Red)
      ) {
         Text("Number: $rememberedNumber")
         Text("VM: ${viewModel<TestAndroidXViewModel>().hashCode()}")

         logcat { "Log inside ScreenA" }

         Button(onClick = { navigator.navigateTo(ScreenBKey(resultKey)) }) {
            TopLevelFunction()
         }

         Button(onClick = { navigator.navigateTo(ProductListScreenKey) }) {
            Text("Products")
         }

         Button(onClick = { navigator.navigateTo(MasterDetailDemoScreenKey) }) {
            Text("Master Detail")
         }
      }
   }
}

@Composable
private fun TopLevelFunction() {
   logcat("TopLevelFunction") { "Log inside top level function" }
   Text("Go to screen B ")
}
