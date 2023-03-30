package si.inova.androidarchitectureplayground.simplestack

import androidx.compose.runtime.Composable
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.GlobalServices
import si.inova.androidarchitectureplayground.di.NavigationInjection
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

@Composable
fun NavigationInjection.Factory.rememberBackstack(
   id: String = "SINGLE",
   interceptBackButton: Boolean = true,
   overrideMainBackstack: Backstack? = null,
   initialHistory: () -> List<ScreenKey>
): Backstack {
   return rememberBackstack(id, interceptBackButton, init = {
      val scopedServices = MyScopedServices()

      lateinit var navigationInjection: NavigationInjection

      val backstack = createBackstack(
         initialHistory(),
         scopedServices = scopedServices,
         globalServicesFactory = {
            GlobalServices.builder()
               .addService(NavigationInjection::class.java.name, navigationInjection)
               .build()
         }
      )

      navigationInjection = create(backstack, overrideMainBackstack ?: backstack)

      backstack
   })
}
