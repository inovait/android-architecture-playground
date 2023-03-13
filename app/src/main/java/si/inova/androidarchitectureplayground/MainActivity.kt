package si.inova.androidarchitectureplayground

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.deliveryhero.whetstone.Whetstone
import com.deliveryhero.whetstone.activity.ContributesActivityInjector
import com.zhuinden.simplestack.AsyncStateChanger
import com.zhuinden.simplestack.History
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import si.inova.androidarchitectureplayground.di.NavigationStackComponent
import si.inova.androidarchitectureplayground.migration.NavigatorActivity
import si.inova.androidarchitectureplayground.navigation.base.DeepLinkHandler
import si.inova.androidarchitectureplayground.navigation.base.Screen
import si.inova.androidarchitectureplayground.navigation.instructions.NavigationInstruction
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey
import si.inova.androidarchitectureplayground.simplestack.BackstackProvider
import si.inova.androidarchitectureplayground.simplestack.ComposeStateChanger
import si.inova.androidarchitectureplayground.simplestack.MyScopedServices
import si.inova.androidarchitectureplayground.simplestack.NavigationContextImpl
import si.inova.androidarchitectureplayground.simplestack.rememberBackstack
import si.inova.androidarchitectureplayground.time.AndroidDateTimeFormatter
import si.inova.androidarchitectureplayground.ui.theme.AndroidArchitecturePlaygroundTheme
import si.inova.androidarchitectureplayground.ui.time.ComposeAndroidDateTimeFormatter
import si.inova.androidarchitectureplayground.ui.time.LocalDateFormatter
import javax.inject.Inject
import javax.inject.Provider

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
            var screenFactories: Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Provider<Screen<*>>>? = null

            val composeStateChanger = remember {
               ComposeStateChanger(screenFactories = lazy { requireNotNull(screenFactories) })
            }

            val asyncStateChanger = remember() { AsyncStateChanger(composeStateChanger) }
            val backstack = rememberBackstack(asyncStateChanger) {
               val scopedServices = MyScopedServices()

               val backstack = createBackstack(
                  initialHistory,
                  scopedServices = scopedServices
               )

               val activityComponent = navigationStackComponentFactory.create(backstack, backstack)
               screenFactories = activityComponent.screenFactories()
               scopedServices.scopedServicesFactories = activityComponent.scopedServicesFactories()
               scopedServices.scopedServicesKeys = activityComponent.scopedServicesKeys()
               navigator = activityComponent.navigator()

               backstack
            }

            if (screenFactories == null) {
               val component = navigationStackComponentFactory.create(backstack, backstack)
               screenFactories = component.screenFactories()
               navigator = component.navigator()
            }

            FragmentTransaction.TRANSIT_FRAGMENT_OPEN

            CompositionLocalProvider(LocalDateFormatter provides ComposeAndroidDateTimeFormatter(dateFormatter)) {
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
      }

      initComplete = true
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
