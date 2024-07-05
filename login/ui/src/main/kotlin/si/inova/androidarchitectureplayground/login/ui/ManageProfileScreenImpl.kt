package si.inova.androidarchitectureplayground.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import si.inova.androidarchitectureplayground.navigation.keys.ManageProfileScreenKey
import si.inova.androidarchitectureplayground.ui.showkase.ShowkaseLauncher
import si.inova.kotlinova.compose.flow.collectAsStateWithLifecycleAndBlinkingPrevention
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.di.ContributesScreenBinding
import si.inova.kotlinova.navigation.screens.Screen

@ContributesScreenBinding
@Stable
class ManageProfileScreenImpl(
   private val viewModel: ManageProfileScreenViewModel,
   private val showkaseLauncher: ShowkaseLauncher,
) : Screen<ManageProfileScreenKey>() {
   @Composable
   override fun Content(key: ManageProfileScreenKey) {
      Column(
         Modifier.fillMaxSize(),
         verticalArrangement = Arrangement.Center,
         horizontalAlignment = Alignment.CenterHorizontally
      ) {
         val status = viewModel.logoutStatus.collectAsStateWithLifecycleAndBlinkingPrevention().value
         if (status != null) {
            if (status is Outcome.Progress) {
               CircularProgressIndicator()
            } else {
               Button(onClick = { viewModel.logout() }) {
                  Text(stringResource(R.string.logout))
               }
            }
         }

         if (BuildConfig.DEBUG) {
            val context = LocalContext.current
            Button(onClick = { showkaseLauncher.launch(context) }) {
               Text("Components")
            }
         }
      }
   }
}
