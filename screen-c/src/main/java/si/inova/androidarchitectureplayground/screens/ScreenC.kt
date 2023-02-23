package si.inova.androidarchitectureplayground.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import si.inova.androidarchitectureplayground.navigation.Navigator
import si.inova.androidarchitectureplayground.navigation.base.Screen
import kotlin.math.roundToInt

class ScreenC(
   private val navigator: Navigator,
   private val viewModel: ScreenCViewModel
) : Screen<ScreenCKey>() {
   @Composable
   override fun Content(key: ScreenCKey) {
      Column(
         Modifier
            .fillMaxSize()
            .background(Color.Blue)
      ) {
         Text("Key: ${key.number} from ${viewModel.number}")

         Button(onClick = { navigator.navigateTo(ScreenCKey(key.number + 1)) }) {
            Text("Open another C")
         }

         Button(onClick = { navigator.goBack() }) {
            Text("Go back")
         }

         @Suppress("MagicNumber") // Just for testing
         val keyBasedAnimation = animateFloatAsState(key.number * 32f)
         Box(
            Modifier
               .offset { IntOffset(0, keyBasedAnimation.value.roundToInt()) }
               .size(32.dp, 32.dp)
               .background(Color.Red)
         )
      }
   }
}
