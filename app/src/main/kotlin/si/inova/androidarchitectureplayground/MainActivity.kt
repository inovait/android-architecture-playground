package si.inova.androidarchitectureplayground

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.deliveryhero.whetstone.Whetstone
import com.deliveryhero.whetstone.activity.ContributesActivityInjector
import com.zhuinden.simplestack.AsyncStateChanger
import com.zhuinden.simplestack.History
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import si.inova.androidarchitectureplayground.di.NavigationInjection
import si.inova.androidarchitectureplayground.di.NavigationStackComponent
import si.inova.androidarchitectureplayground.migration.NavigatorActivity
import si.inova.androidarchitectureplayground.navigation.base.DeepLinkHandler
import si.inova.androidarchitectureplayground.navigation.instructions.NavigationInstruction
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey
import si.inova.androidarchitectureplayground.simplestack.BackstackProvider
import si.inova.androidarchitectureplayground.simplestack.ComposeStateChanger
import si.inova.androidarchitectureplayground.simplestack.NavigationContextImpl
import si.inova.androidarchitectureplayground.simplestack.rememberBackstack
import si.inova.androidarchitectureplayground.ui.result.LocalResultPassingStore
import si.inova.androidarchitectureplayground.ui.result.ResultPassingStore
import si.inova.androidarchitectureplayground.ui.theme.AndroidArchitecturePlaygroundTheme
import si.inova.androidarchitectureplayground.ui.time.ComposeAndroidDateTimeFormatter
import si.inova.androidarchitectureplayground.ui.time.LocalDateFormatter
import si.inova.kotlinova.core.time.AndroidDateTimeFormatter
import javax.inject.Inject

@ContributesActivityInjector
class MainActivity : FragmentActivity(), NavigatorActivity {
   @Inject
   lateinit var navigationStackComponentFactory: NavigationStackComponent.Factory

   @Inject
   lateinit var deepLinkHandlers: Set<@JvmSuppressWildcards DeepLinkHandler>

   override lateinit var navigator: si.inova.androidarchitectureplayground.navigation.Navigator

   @Inject
   lateinit var navigationContext: NavigationContextImpl

   @Inject
   lateinit var dateFormatter: AndroidDateTimeFormatter

   private val viewModel by viewModels<MainViewModel>()
   private var initComplete = false

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      Whetstone.inject(this)

      val splashScreen = installSplashScreen()
      splashScreen.setKeepOnScreenCondition { !initComplete }

      beginInitialisation()
   }

   private fun beginInitialisation() {
      lifecycleScope.launch {
         var initialHistory: List<ScreenKey> = History.of(viewModel.startingScreen.filterNotNull().first())
         val deepLinkTarget = intent?.data?.let { getDeepLinkTarget(it, startup = true) }
         if (deepLinkTarget != null) {
            initialHistory = deepLinkTarget.performNavigation(initialHistory, navigationContext).newBackstack
         }

         setContent {
            NavigationRoot(initialHistory)
         }

         initComplete = true
      }
   }

   @Composable
   private fun NavigationRoot(initialHistory: List<ScreenKey>) {
      val backstack = navigationStackComponentFactory.rememberBackstack { initialHistory }

      val composeStateChanger = ComposeStateChanger()
      remember(composeStateChanger) {
         backstack.setStateChanger(AsyncStateChanger(composeStateChanger))
         true
      }

      remember(backstack) {
         navigator = NavigationInjection.fromBackstack(backstack).navigator()
         true
      }

      val resultPassingStore = rememberSaveable { ResultPassingStore() }

      CompositionLocalProvider(
         LocalDateFormatter provides ComposeAndroidDateTimeFormatter(dateFormatter),
         LocalResultPassingStore provides resultPassingStore
      ) {
         AndroidArchitecturePlaygroundTheme {
            // A surface container using the 'background' color from the theme
            Surface(
               modifier = Modifier.fillMaxSize(),
               color = MaterialTheme.colorScheme.background
            ) {
               BackstackProvider(backstack) {
                  composeStateChanger.Content()
               }
            }
         }
      }
   }

   override fun onNewIntent(intent: Intent?) {
      super.onNewIntent(intent)

      if (!initComplete) {
         this.intent = intent
         return
      }

      intent?.data?.let { url ->
         val navigationKey = getDeepLinkTarget(url, startup = false)
         navigationKey?.let {
            navigator.navigate(it)
         }
      }
   }

   private fun getDeepLinkTarget(uri: Uri, startup: Boolean): NavigationInstruction? {
      return deepLinkHandlers.asSequence<@JvmSuppressWildcards DeepLinkHandler>()
         .mapNotNull<@JvmSuppressWildcards DeepLinkHandler, NavigationInstruction> { it.handleDeepLink(uri, startup) }
         .firstOrNull()
   }
}
