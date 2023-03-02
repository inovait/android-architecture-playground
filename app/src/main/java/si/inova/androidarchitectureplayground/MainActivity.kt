package si.inova.androidarchitectureplayground

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.deliveryhero.whetstone.Whetstone
import com.deliveryhero.whetstone.activity.ContributesActivityInjector
import com.zhuinden.simplestack.AsyncStateChanger
import com.zhuinden.simplestack.History
import si.inova.androidarchitectureplayground.di.NavigationStackComponent
import si.inova.androidarchitectureplayground.navigation.base.DeepLinkHandler
import si.inova.androidarchitectureplayground.navigation.base.Screen
import si.inova.androidarchitectureplayground.navigation.instructions.NavigationInstruction
import si.inova.androidarchitectureplayground.navigation.keys.ScreenAKey
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey
import si.inova.androidarchitectureplayground.simplestack.BackstackProvider
import si.inova.androidarchitectureplayground.simplestack.ComposeStateChanger
import si.inova.androidarchitectureplayground.simplestack.MyScopedServices
import si.inova.androidarchitectureplayground.simplestack.NavigationContextImpl
import si.inova.androidarchitectureplayground.simplestack.rememberBackstack
import si.inova.androidarchitectureplayground.ui.theme.AndroidArchitecturePlaygroundTheme
import javax.inject.Inject
import javax.inject.Provider

@ContributesActivityInjector
class MainActivity : FragmentActivity() {
   @Inject
   lateinit var navigationStackComponentFactory: NavigationStackComponent.Factory

   @Inject
   lateinit var deepLinkHandlers: Set<@JvmSuppressWildcards DeepLinkHandler>

   lateinit var navigator: si.inova.androidarchitectureplayground.navigation.Navigator

   @Inject
   lateinit var navigationContext: NavigationContextImpl

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      Whetstone.inject(this)

      val deepLinkTarget = intent?.data?.let { getDeepLinkTarget(it, startup = true) }
      var initialHistory: List<ScreenKey> = History.of(ScreenAKey)
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
         }

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
