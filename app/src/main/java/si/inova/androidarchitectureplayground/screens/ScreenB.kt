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
import com.zhuinden.simplestack.StateChange

class ScreenB(
   private val backstack: Backstack,
   private val viewModel: SharedViewModel
) : Screen<ScreenBKey>() {
   @Composable
   override fun Content() {
      Column(
         Modifier
            .fillMaxSize()
            .background(Color.Green)
      ) {
         Text("ViewModel: $viewModel")
         Button(onClick = { backstack.replaceTop(ScreenCKey, StateChange.REPLACE) }) {
            Text("Replace with C ")
         }
      }
   }
}
