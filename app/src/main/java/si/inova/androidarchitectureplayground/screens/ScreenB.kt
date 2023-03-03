package si.inova.androidarchitectureplayground.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhuinden.simplestack.History
import si.inova.androidarchitectureplayground.migration.TestFragmentKey
import si.inova.androidarchitectureplayground.navigation.Navigator
import si.inova.androidarchitectureplayground.navigation.base.Screen
import si.inova.androidarchitectureplayground.navigation.instructions.navigateTo
import si.inova.androidarchitectureplayground.navigation.keys.ScreenBKey
import si.inova.androidarchitectureplayground.navigation.nested.NestedNavigator
import si.inova.androidarchitectureplayground.screens.nested.NestedScreenAKey

class ScreenB(
   private val navigator: Navigator,
   private val viewModel: SharedViewModel,
   private val nestedNavigator: NestedNavigator
) : Screen<ScreenBKey>() {
   @Composable
   override fun Content(key: ScreenBKey) {
      Column(
         Modifier
            .fillMaxSize()
            .background(Color.Green)
      ) {
         Text("ViewModel: $viewModel ${viewModel.number}")
         Text("android VM: ${viewModel<TestAndroidXViewModel>().hashCode()}")

         Box(
            Modifier
               .padding(64.dp)
               .background(Color.Red)
         ) {
            nestedNavigator.NestedNavigation { History.of(NestedScreenAKey()) }
         }

         Button(onClick = { navigator.navigateTo(ScreenCKey(1, "")) }) {
            Text("Replace with C ")
         }

         Button(onClick = { navigator.navigateTo(TestFragmentKey("Hello from B")) }) {
            Text("OpenFragment ")
         }
      }
   }
}
