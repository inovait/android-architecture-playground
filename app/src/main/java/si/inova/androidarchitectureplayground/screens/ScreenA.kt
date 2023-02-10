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
import com.zhuinden.simplestack.Backstack
import kotlin.random.Random

@Suppress("unused")
class ScreenA constructor(
   private val backstack: Backstack,
   private val viewModel: ScreenAViewModel
) : Screen<ScreenAKey>() {
   @Composable
   override fun Content() {
      val rememberedNumber = rememberSaveable() { Random.nextInt() }

      Column(
         Modifier
            .fillMaxSize()
            .background(Color.Red)
      ) {
         Text("Number: $rememberedNumber")

         Button(onClick = { backstack.goTo(ScreenBKey) }) {
            Text("Go to screen B ")
         }
      }
   }
}
