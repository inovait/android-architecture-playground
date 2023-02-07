package si.inova.androidarchitectureplayground.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import si.inova.libmodule.TestQualifier

class ScreenA(
   @TestQualifier
   val helloText: String, private val viewModel: ScreenAViewModel
) : Screen<ScreenAKey>() {
   @Composable
   override fun Content() {
      Text("$helloText $key $viewModel $this")
   }
}
