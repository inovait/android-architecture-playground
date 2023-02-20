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
import si.inova.androidarchitectureplayground.navigation.Navigator
import si.inova.androidarchitectureplayground.navigation.base.Screen
import kotlin.random.Random

@Suppress("unused")
class ScreenA constructor(
   private val viewModel: ScreenAViewModel,
   private val navigator: Navigator
) : Screen<ScreenAKey>() {
   @Composable
   override fun Content(key: ScreenAKey) {
      val rememberedNumber = rememberSaveable() { Random.nextInt() }

      Column(
         Modifier
            .fillMaxSize()
            .background(Color.Red)
      ) {
         Text("Number: $rememberedNumber")
         Text("VM: ${viewModel<TestAndroidXViewModel>().hashCode()}")

         Button(onClick = { navigator.navigateTo(ScreenBKey) }) {
            Text("Go to screen B ")
         }
      }
   }
}
