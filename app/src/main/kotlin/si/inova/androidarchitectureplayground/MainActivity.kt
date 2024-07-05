package si.inova.androidarchitectureplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.zhuinden.simplestack.Backstack
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import si.inova.androidarchitectureplayground.ui.theme.AndroidArchitecturePlaygroundTheme
import si.inova.kotlinova.compose.result.LocalResultPassingStore
import si.inova.kotlinova.compose.result.ResultPassingStore
import si.inova.kotlinova.compose.time.ComposeAndroidDateTimeFormatter
import si.inova.kotlinova.compose.time.LocalDateFormatter
import si.inova.kotlinova.core.time.AndroidDateTimeFormatter
import si.inova.kotlinova.navigation.deeplink.HandleNewIntentDeepLinks
import si.inova.kotlinova.navigation.deeplink.MainDeepLinkHandler
import si.inova.kotlinova.navigation.di.NavigationContext
import si.inova.kotlinova.navigation.di.NavigationInjection
import si.inova.kotlinova.navigation.screenkeys.ScreenKey
import si.inova.kotlinova.navigation.simplestack.RootNavigationContainer
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : ComponentActivity() {
   @Inject
   lateinit var navigationInjectionFactory: NavigationInjection.Factory

   @Inject
   lateinit var mainDeepLinkHandler: MainDeepLinkHandler

   @Inject
   lateinit var navigationContext: NavigationContext

   @Inject
   lateinit var dateFormatter: AndroidDateTimeFormatter

   @Inject
   lateinit var viewModelProvider: Provider<MainViewModel>

   private val viewModel by viewModels<MainViewModel>() { ViewModelFactory() }
   private var initComplete = false

   override fun onCreate(savedInstanceState: Bundle?) {
      (requireNotNull(application) as MyApplication).applicationComponent.inject(this)
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()

      val splashScreen = installSplashScreen()
      splashScreen.setKeepOnScreenCondition { !initComplete }

      beginInitialisation(savedInstanceState == null)
   }

   private fun beginInitialisation(startup: Boolean) {
      lifecycleScope.launch {
         val initialHistory: ImmutableList<ScreenKey> = persistentListOf(viewModel.startingScreen.filterNotNull().first())

         val deepLinkTarget = if (startup) {
            intent?.data?.let { mainDeepLinkHandler.handleDeepLink(it, startup = true) }
         } else {
            null
         }

         val overridenInitialHistoryFromDeepLink = if (deepLinkTarget != null) {
            deepLinkTarget.performNavigation(initialHistory, navigationContext).newBackstack.toPersistentList()
         } else {
            initialHistory
         }

         setContent {
            NavigationRoot(overridenInitialHistoryFromDeepLink)
         }

         initComplete = true
      }
   }

   @Composable
   private fun NavigationRoot(initialHistory: ImmutableList<ScreenKey>) {
      AndroidArchitecturePlaygroundTheme {
         // A surface container using the 'background' color from the theme
         Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
         ) {
            val resultPassingStore = rememberSaveable { ResultPassingStore() }
            CompositionLocalProvider(
               LocalDateFormatter provides ComposeAndroidDateTimeFormatter(dateFormatter),
               LocalResultPassingStore provides resultPassingStore
            ) {
               val backstack = navigationInjectionFactory.RootNavigationContainer(
                  initialHistory = { initialHistory },
                  screenWrapper = { _, screen ->
                     Surface {
                        screen()
                     }
                  }
               )

               LogCurrentScreen(backstack)

               mainDeepLinkHandler.HandleNewIntentDeepLinks(this@MainActivity, backstack)
            }
         }
      }
   }

   private inner class ViewModelFactory : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
         @Suppress("UNCHECKED_CAST")
         return requireNotNull(viewModelProvider.get()) as T
      }
   }
}

@Composable
private fun LogCurrentScreen(backstack: Backstack) {
   DisposableEffect(backstack) {
      val listener = Backstack.CompletionListener {
         @Suppress("UNUSED_VARIABLE") // TODO use it
         val newTopKey = it.topNewKey<ScreenKey>()

         // TODO log new top key here to the crash reporting service, such as Firebase
         //  (and ideally set a Key) to make debugging crashes / error reports easier
      }

      backstack.addStateChangeCompletionListener(listener)

      onDispose { backstack.removeStateChangeCompletionListener(listener) }
   }
}
