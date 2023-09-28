package si.inova.androidarchitectureplayground.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import si.inova.androidarchitectureplayground.navigation.keys.ManageProfileScreenKey
import si.inova.androidarchitectureplayground.ui.showkase.ShowkaseLauncher
import si.inova.kotlinova.compose.flow.collectAsStateWithLifecycleAndBlinkingPrevention
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.di.ContributesScreenBinding
import si.inova.kotlinova.navigation.screens.Screen

@ContributesScreenBinding
class ManageProfileScreenImpl(
   private val viewModel: ManageProfileScreenViewModel,
   private val showkaseLauncher: ShowkaseLauncher,
   private val counterViewModel: CounterViewModel
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

            val coroutineScope = rememberCoroutineScope()

            Button(onClick = { coroutineScope.launch { with(FlowDemo) { start() } } }) {
               Text("Flow Demo")
            }

            val paused by counterViewModel.paused.collectAsStateWithLifecycle()
            Button(modifier = Modifier.padding(top = 32.dp), onClick = { counterViewModel.startCounting() }) {
               Text("Start counting")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
               Checkbox(paused, onCheckedChange = { counterViewModel.setPaused(it) })
               Text("Paused")
            }

            val number by counterViewModel.counterText.collectAsStateWithLifecycle()
            Text(number.data ?: "", fontSize = 32.sp)
         }
      }
   }
}
