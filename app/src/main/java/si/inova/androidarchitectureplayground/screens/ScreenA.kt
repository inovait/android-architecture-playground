package si.inova.androidarchitectureplayground.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import javax.inject.Inject

class ScreenA @Inject constructor() : Screen<ScreenAKey> {
   @Composable
   override fun Content() {
      Text("Screen A")
   }
}
