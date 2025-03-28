package si.inova.androidarchitectureplayground.ui.debugging

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import si.inova.androidarchitectureplayground.ui.theme.AndroidArchitecturePlaygroundTheme
import si.inova.kotlinova.compose.preview.FakeCoilLoader
import si.inova.kotlinova.compose.time.ComposeAndroidDateTimeFormatter
import si.inova.kotlinova.compose.time.LocalDateFormatter
import si.inova.kotlinova.core.time.AndroidDateTimeFormatter
import si.inova.kotlinova.core.time.FakeAndroidDateTimeFormatter

@OptIn(DelicateCoilApi::class)
@Composable
@Suppress("ModifierMissing") // This is intentional
fun PreviewTheme(
   formatter: AndroidDateTimeFormatter = FakeAndroidDateTimeFormatter(),
   fill: Boolean = true,
   content: @Composable () -> Unit,
) {
   // SetUnsafe ensures that same coil loader is reused
   SingletonImageLoader.setUnsafe(FakeCoilLoader())

   CompositionLocalProvider(LocalDateFormatter provides ComposeAndroidDateTimeFormatter(formatter)) {
      // Disable Material You on previews (and screenshot tests) to improve reproducibility
      AndroidArchitecturePlaygroundTheme(dynamicColor = false) {
         Surface(modifier = if (fill) Modifier.fillMaxSize() else Modifier, content = content)
      }
   }
}
