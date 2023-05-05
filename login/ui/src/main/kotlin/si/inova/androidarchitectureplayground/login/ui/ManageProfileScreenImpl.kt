package si.inova.androidarchitectureplayground.login.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import si.inova.androidarchitectureplayground.navigation.keys.ManageProfileScreenKey
import si.inova.kotlinova.compose.flow.collectAsStateWithLifecycleAndBlinkingPrevention
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.di.ContributesScreenBinding
import si.inova.kotlinova.navigation.screens.Screen

@ContributesScreenBinding
class ManageProfileScreenImpl(
   private val viewModel: ManageProfileScreenViewModel
) : Screen<ManageProfileScreenKey>() {
   @Composable
   override fun Content(key: ManageProfileScreenKey) {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
      }
   }
}