package si.inova.androidarchitectureplayground.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhuinden.simplestack.History
import si.inova.androidarchitectureplayground.migration.TestFragmentKey
import si.inova.androidarchitectureplayground.navigation.Navigator
import si.inova.androidarchitectureplayground.navigation.base.Screen
import si.inova.androidarchitectureplayground.navigation.instructions.goBack
import si.inova.androidarchitectureplayground.navigation.instructions.navigateTo
import si.inova.androidarchitectureplayground.navigation.keys.ScreenBKey
import si.inova.androidarchitectureplayground.navigation.nested.NestedBackstackScreen
import si.inova.androidarchitectureplayground.navigation.nested.NestedNavigationScreenKey
import si.inova.androidarchitectureplayground.screens.nested.NestedScreenAKey
import si.inova.androidarchitectureplayground.ui.result.LocalResultPassingStore
import si.inova.androidarchitectureplayground.ui.time.ComposeAndroidDateTimeFormatter
import si.inova.androidarchitectureplayground.ui.time.LocalDateFormatter
import si.inova.kotlinova.core.time.AndroidTimeProvider
import si.inova.kotlinova.core.time.FakeAndroidDateTimeFormatter
import si.inova.kotlinova.core.time.FakeAndroidTimeProvider
import java.time.LocalDateTime
import java.time.format.FormatStyle

class ScreenB(
   private val navigator: Navigator,
   private val viewModel: SharedViewModel,
   private val nestedBackstackScreen: NestedBackstackScreen,
   private val timeProvider: AndroidTimeProvider
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

         CurrentTime(timeProvider)
         Box(
            Modifier
               .padding(64.dp)
               .background(Color.Red)
         ) {
            nestedBackstackScreen.Content(NestedNavigationScreenKey(History.of(NestedScreenAKey)))
         }

         Button(onClick = { navigator.navigateTo(ScreenCKey(1, "")) }) {
            Text("Replace with C ")
         }

         val store = LocalResultPassingStore.current

         Button(onClick = {
            key.result?.let { store.sendResult(it, "Hello from B") }
            navigator.goBack()
         }) {
            Text("Go back to A with result ")
         }

         Button(onClick = { navigator.navigateTo(TestFragmentKey("Hello from B")) }) {
            Text("OpenFragment ")
         }
      }
   }
}

@Composable
private fun CurrentTime(timeProvider: AndroidTimeProvider) {
   val time = LocalDateFormatter.current.ofLocalizedDateTime(FormatStyle.FULL).format(timeProvider.currentZonedDateTime())
   Text(
      "Current time: $time"
   )
}

@Preview
@Composable
private fun CurrentTimePreview() {
   val timeProvider = FakeAndroidTimeProvider(
      currentLocalDateTime = { LocalDateTime.of(2020, 1, 12, 12, 30) }
   )

   val formatter = FakeAndroidDateTimeFormatter(
      use24hTime = false
   )

   CompositionLocalProvider(LocalDateFormatter provides ComposeAndroidDateTimeFormatter(formatter)) {
      CurrentTime(timeProvider)
   }
}
