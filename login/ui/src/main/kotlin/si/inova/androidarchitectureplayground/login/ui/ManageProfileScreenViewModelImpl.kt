package si.inova.androidarchitectureplayground.login.ui

import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import si.inova.androidarchitectureplayground.common.logging.ActionLogger
import si.inova.androidarchitectureplayground.home.HomeScreenKey
import si.inova.androidarchitectureplayground.login.LoginRepository
import si.inova.androidarchitectureplayground.login.LoginScreenKey
import si.inova.androidarchitectureplayground.login.ManageProfileScreenKey
import si.inova.androidarchitectureplayground.navigation.services.BaseViewModel
import si.inova.androidarchitectureplayground.post.PostListScreenKey
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.di.BackstackScope
import si.inova.kotlinova.navigation.instructions.MultiNavigationInstructions
import si.inova.kotlinova.navigation.instructions.OpenScreen
import si.inova.kotlinova.navigation.instructions.ReplaceBackstack
import si.inova.kotlinova.navigation.navigator.Navigator
import si.inova.kotlinova.navigation.services.ScopedService

@ContributesIntoMap(BackstackScope::class, binding = binding<ScopedService>())
@ClassKey(ManageProfileScreenViewModel::class)
internal class ManageProfileScreenViewModelImpl(
   resourcesFactory: CoroutineResourceManager.Factory,
   private val navigator: Navigator,
   private val loginRepository: LoginRepository,
   private val actionLogger: ActionLogger,
) : BaseViewModel<ManageProfileScreenKey>(resourcesFactory), ManageProfileScreenViewModel {
   override val logoutStatus = MutableStateFlow<Outcome<Unit>>(Outcome.Success(Unit))
   override fun logout() {
      actionLogger.logAction { "logout" }
      resources.launchResourceControlTask(logoutStatus) {
         @Suppress("MagicNumber") // Delay is here just for demo purposes
         delay(500)
         loginRepository.setLoggedIn(false)
         emit(Outcome.Success(Unit))

         navigator.navigate(
            ReplaceBackstack(
               LoginScreenKey(
                  MultiNavigationInstructions(
                     OpenScreen(HomeScreenKey),
                     OpenScreen(PostListScreenKey)
                  )
               )
            )
         )
      }
   }
}
