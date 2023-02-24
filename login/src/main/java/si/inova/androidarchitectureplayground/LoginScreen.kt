package si.inova.androidarchitectureplayground

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import si.inova.androidarchitectureplayground.navigation.base.Screen
import si.inova.androidarchitectureplayground.navigation.keys.LoginScreenKey

class LoginScreen(private val viewModel: LoginViewModel) : Screen<LoginScreenKey>() {
   @Composable
   override fun Content(key: LoginScreenKey) {
      val loggedIn = viewModel.isLoggedIn.collectAsState()

      Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
         Row {
            Text("Is logged in: ")
            Switch(loggedIn.value, onCheckedChange = viewModel::setLoggedIn)
         }

         Button(onClick = viewModel::finishLogin) {
            Text("Finish")
         }
      }
   }
}
