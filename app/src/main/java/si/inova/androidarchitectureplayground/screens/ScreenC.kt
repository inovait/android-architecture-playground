package si.inova.androidarchitectureplayground.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.zhuinden.simplestack.Backstack

class ScreenC(
   private val backstack: Backstack,
   private val sharedViewModel: SharedViewModel
) : Screen<ScreenCKey>() {
   @Composable
   override fun Content() {
      Column(
         Modifier
            .fillMaxSize()
            .background(Color.Blue)
      ) {
         Text("ViewModel: $sharedViewModel")
         Button(onClick = { backstack.goBack() }) {
            Text("Go back")
         }
      }
   }
}
