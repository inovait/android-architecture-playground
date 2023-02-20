package si.inova.androidarchitectureplayground.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import si.inova.androidarchitectureplayground.navigation.Navigator
import si.inova.androidarchitectureplayground.navigation.base.Screen

class ScreenB(
   private val navigator: Navigator,
   private val viewModel: SharedViewModel
) : Screen<ScreenBKey>() {
   @Composable
   override fun Content(key: ScreenBKey) {
      Column(
         Modifier
            .fillMaxSize()
            .background(Color.Green)
      ) {
         Text("ViewModel: $viewModel")
         Text("android VM: ${viewModel<TestAndroidXViewModel>().hashCode()}")
         Button(onClick = { navigator.navigateTo(ScreenCKey(1)) }) {
            Text("Replace with C ")
         }
      }
   }
}
