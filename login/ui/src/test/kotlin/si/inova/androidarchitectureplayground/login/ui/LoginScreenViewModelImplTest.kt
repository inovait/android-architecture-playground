package si.inova.androidarchitectureplayground.login.ui

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.login.FakeLoginRepository
import si.inova.androidarchitectureplayground.navigation.keys.LoginScreenKey
import si.inova.kotlinova.core.test.outcomes.shouldBeSuccessWithData
import si.inova.kotlinova.core.test.outcomes.testCoroutineResourceManager
import si.inova.kotlinova.navigation.instructions.GoBack
import si.inova.kotlinova.navigation.instructions.NavigationInstruction
import si.inova.kotlinova.navigation.instructions.OpenScreen
import si.inova.kotlinova.navigation.screenkeys.ScreenKey
import si.inova.kotlinova.navigation.test.FakeNavigator

class LoginScreenViewModelImplTest {
   val scope = TestScope()
   val navigator = FakeNavigator(LoginScreenKey(OpenScreen(AnotherScreenKey())))
   val loginRepository = FakeLoginRepository()

   val viewModel = LoginScreenViewModelImpl(
      scope.testCoroutineResourceManager(),
      navigator,
      loginRepository,
      actionLogger = {}
   )

   @Test
   fun `Return Unit login by default`() = scope.runTest {
      initViewModel()
      runCurrent()

      viewModel.loginStatus.value shouldBeSuccessWithData Unit
   }

   @Test
   fun `Send login to login repository`() = scope.runTest {
      initViewModel()

      viewModel.login()
      advanceTimeBy(1000)

      loginRepository.isLoggedIn shouldBe true
   }

   @Test
   fun `Close login screen and navigate to target instructions`() = scope.runTest {
      initViewModel(OpenScreen(AnotherScreenKey()))

      viewModel.login()
      advanceTimeBy(1000)

      navigator.backstack.shouldContainExactly(
         AnotherScreenKey()
      )
   }

   @Test
   fun `Only close login screen, leave the rest of the backstack alone`() = scope.runTest {
      navigator.backstack = listOf(
         AnotherScreenKey(1),
         AnotherScreenKey(2),
         LoginScreenKey(OpenScreen(AnotherScreenKey(3)))
      )

      initViewModel(OpenScreen(AnotherScreenKey(3)))

      viewModel.login()
      advanceTimeBy(1000)

      navigator.backstack.shouldContainExactly(
         AnotherScreenKey(1),
         AnotherScreenKey(2),
         AnotherScreenKey(3)
      )
   }

   private fun initViewModel(navigationInstruction: NavigationInstruction = GoBack) {
      viewModel.key = LoginScreenKey(navigationInstruction)
      viewModel.onServiceRegistered()
   }

   @Serializable
   private data class AnotherScreenKey(val number: Int = 0) : ScreenKey()
}
