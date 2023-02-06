package si.inova.androidarchitectureplayground

import android.content.res.Resources
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import com.deliveryhero.whetstone.Whetstone
import com.deliveryhero.whetstone.activity.ContributesActivityInjector
import si.inova.androidarchitectureplayground.screens.Screen
import si.inova.androidarchitectureplayground.ui.theme.AndroidArchitecturePlaygroundTheme
import javax.inject.Inject
import javax.inject.Provider

@ContributesActivityInjector
class MainActivity : FragmentActivity() {
   @Inject
   lateinit var injectedResources: Resources

   @Inject
   lateinit var screens: Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Provider<Screen<*>>>

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      Whetstone.inject(this)

      setContent {
         AndroidArchitecturePlaygroundTheme {
            // A surface container using the 'background' color from the theme
            Surface(
               modifier = Modifier.fillMaxSize(),
               color = MaterialTheme.colorScheme.background
            ) {
               Greeting("Android")
            }
         }
      }
   }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
   Text(
      text = "Hello $name!",
      modifier = modifier
   )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
   AndroidArchitecturePlaygroundTheme {
      Greeting("Android")
   }
}
