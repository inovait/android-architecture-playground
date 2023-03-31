package si.inova.androidarchitectureplayground.migration

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setContainerAvailable
import com.zhuinden.simplestack.ScopedServices
import si.inova.androidarchitectureplayground.navigation.base.ScopedService
import si.inova.androidarchitectureplayground.navigation.base.Screen
import si.inova.kotlinova.core.activity.requireActivity
import java.lang.ref.WeakReference
import javax.inject.Inject
import kotlin.random.Random

class FragmentScreen(
   private val scopeExitListener: ScopeExitListener
) : Screen<FragmentScreenKey>() {
   @Composable
   override fun Content(key: FragmentScreenKey) {
      val activity = LocalContext.current.requireActivity()

      View.generateViewId()
      val fragmentViewId = rememberSaveable { Random.nextInt(MAX_VIEW_ID) }

      AndroidView({ context ->
         FragmentContainerView(context).apply { id = fragmentViewId }
      })

      DisposableEffect(key, fragmentViewId) {
         val fragmentManager = activity.supportFragmentManager
         var currentFragment = fragmentManager.findFragmentByTag(key.tag)

         if (currentFragment == null) {
            currentFragment = fragmentManager.fragmentFactory.instantiate(
               requireNotNull(javaClass.classLoader),
               key.fragmentClass
            ).also { it.arguments = key.arguments }

            fragmentManager
               .beginTransaction()
               .replace(fragmentViewId, currentFragment, key.tag)
               .commit()
         }

         scopeExitListener.fragmentPair = WeakReference(fragmentManager to currentFragment)

         if (currentFragment.isDetached) {
            fragmentManager
               .beginTransaction()
               .attach(currentFragment)
               .commit()
         }

         fragmentManager.setContainerAvailable(activity.findViewById(fragmentViewId))

         onDispose {
            if (!fragmentManager.isStateSaved) {
               fragmentManager
                  .beginTransaction()
                  .detach(currentFragment)
                  .commit()
            }
         }
      }
   }
}

class ScopeExitListener @Inject constructor() : ScopedServices.Registered, ScopedService {
   var fragmentPair: WeakReference<Pair<FragmentManager, Fragment>> = WeakReference(null)

   override fun onServiceRegistered() {}

   override fun onServiceUnregistered() {
      fragmentPair.get()?.let { (fragmentManager, fragment) ->
         if (!fragment.isStateSaved) {
            fragmentManager.beginTransaction()
               .remove(fragment)
               .commit()
         }
      }
   }
}

private const val MAX_VIEW_ID = 0x00FFFFFF
