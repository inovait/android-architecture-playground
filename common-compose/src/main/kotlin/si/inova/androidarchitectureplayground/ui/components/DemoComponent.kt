package si.inova.androidarchitectureplayground.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import si.inova.androidarchitectureplayground.ui.debugging.PreviewTheme

@Composable
fun DemoComponent(modifier: Modifier = Modifier) {
   Box(
      modifier
         .background(MaterialTheme.colorScheme.primary)
         .size(100.dp, 50.dp)
   ) {
      Text("Hello", color = MaterialTheme.colorScheme.onPrimary)
   }
}

@Preview
@Composable
@ShowkaseComposable(group = "Components", name = "Demo Component", styleName = "Normal")
internal fun DemoComponentPreview() {
   PreviewTheme(fill = false) {
      DemoComponent()
   }
}
