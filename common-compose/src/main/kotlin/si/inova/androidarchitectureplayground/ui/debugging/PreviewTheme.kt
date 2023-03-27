package si.inova.androidarchitectureplayground.ui.debugging

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import coil.Coil
import si.inova.androidarchitectureplayground.time.AndroidDateTimeFormatter
import si.inova.androidarchitectureplayground.time.FakeDateTimeFormatter
import si.inova.androidarchitectureplayground.ui.theme.AndroidArchitecturePlaygroundTheme
import si.inova.androidarchitectureplayground.ui.time.ComposeAndroidDateTimeFormatter
import si.inova.androidarchitectureplayground.ui.time.LocalDateFormatter

@Composable
fun PreviewTheme(formatter: AndroidDateTimeFormatter = FakeDateTimeFormatter(), content: @Composable () -> Unit) {
   Coil.setImageLoader(FakeCoilLoader())

   CompositionLocalProvider(LocalDateFormatter provides ComposeAndroidDateTimeFormatter(formatter)) {
      AndroidArchitecturePlaygroundTheme {
         Surface(content = content)
      }
   }
}
