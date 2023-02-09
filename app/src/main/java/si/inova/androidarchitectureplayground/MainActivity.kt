package si.inova.androidarchitectureplayground

import android.content.res.Resources
import android.os.Bundle
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.deliveryhero.whetstone.Whetstone
import com.deliveryhero.whetstone.activity.ContributesActivityInjector
import com.zhuinden.simplestack.AsyncStateChanger
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.navigator.Navigator
import si.inova.androidarchitectureplayground.di.SimpleStackActivityComponent
import si.inova.androidarchitectureplayground.screens.Screen
import si.inova.androidarchitectureplayground.screens.ScreenAKey
import si.inova.androidarchitectureplayground.simplestack.BackstackProvider
import si.inova.androidarchitectureplayground.simplestack.ComposeStateChanger
import si.inova.androidarchitectureplayground.ui.theme.AndroidArchitecturePlaygroundTheme
import javax.inject.Inject
import javax.inject.Provider

@ContributesActivityInjector
class MainActivity : FragmentActivity() {
   @Inject
   lateinit var injectedResources: Resources

   @Inject
   lateinit var activityComponentFactory: SimpleStackActivityComponent.Factory

   lateinit var composeStateChanger: ComposeStateChanger

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      Whetstone.inject(this)

      lateinit var screenFactories: Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Provider<Screen<*>>>

      composeStateChanger = ComposeStateChanger(screenFactories = lazy { screenFactories })

      val scopedServices = MyScopedServices()

      val backstack = Navigator.configure()
         .setStateChanger(AsyncStateChanger(composeStateChanger))
         .setScopedServices(scopedServices)
         .setDeferredInitialization(true)
         .install(this, findViewById(Window.ID_ANDROID_CONTENT), History.of(ScreenAKey))

      val activityComponent = activityComponentFactory.create(backstack)
      screenFactories = activityComponent.screenFactories()
      scopedServices.scopedServicesFactories = activityComponent.scopedServicesFactories()
      scopedServices.scopedServicesKeys = activityComponent.scopedServicesKeys()

      Navigator.executeDeferredInitialization(this)

      onBackPressedDispatcher.addCallback(simpleStackBackPressedCallback)


      setContent {
         AndroidArchitecturePlaygroundTheme {
            // A surface container using the 'background' color from the theme
            Surface(
               modifier = Modifier.fillMaxSize(),
               color = MaterialTheme.colorScheme.background
            ) {
               BackstackProvider(backstack) {
                  composeStateChanger.RenderScreen()
               }
            }
         }
      }
   }

   private val simpleStackBackPressedCallback =
      object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            if (!Navigator.onBackPressed(this@MainActivity)) {
               // Hack until https://github.com/Zhuinden/simple-stack/issues/259 is resolved
               this.remove()
               @Suppress("DEPRECATION")
               onBackPressed()
               this@MainActivity.onBackPressedDispatcher.addCallback(this)
            }
         }
      }
}
