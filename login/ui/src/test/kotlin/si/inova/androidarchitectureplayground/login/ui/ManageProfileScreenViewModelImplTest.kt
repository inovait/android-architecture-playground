package si.inova.androidarchitectureplayground.login.ui

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.parcelize.Parcelize
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.login.FakeLoginRepository
import si.inova.androidarchitectureplayground.navigation.keys.HomeScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.LoginScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.PostListScreenKey
import si.inova.kotlinova.core.test.outcomes.shouldBeSuccessWithData
import si.inova.kotlinova.core.test.outcomes.testCoroutineResourceManager
import si.inova.kotlinova.navigation.instructions.MultiNavigationInstructions
import si.inova.kotlinova.navigation.instructions.OpenScreen
import si.inova.kotlinova.navigation.screenkeys.ScreenKey
import si.inova.kotlinova.navigation.test.FakeNavigator

class ManageProfileScreenViewModelImplTest {
   val scope = TestScope()
   val navigator = FakeNavigator(LoginScreenKey(OpenScreen(AnotherScreenKey())))
   val loginRepository = FakeLoginRepository()

   val viewModel = ManageProfileScreenViewModelImpl(
      scope.testCoroutineResourceManager(),
      navigator,
      loginRepository,
      actionLogger = {}
   )

   @Test
   fun `Return Unit status by default`() = scope.runTest {
      initViewModel()
      runCurrent()

      viewModel.logoutStatus.value shouldBeSuccessWithData Unit
   }

   @Test
   fun `Send logout to login repository`() = scope.runTest {
      loginRepository.setLoggedIn(true)
      initViewModel()

      viewModel.logout()
      advanceTimeBy(1000)

      loginRepository.isLoggedIn shouldBe false
   }

   @Test
   fun `Close login screen and navigate to login`() = scope.runTest {
      navigator.backstack = listOf(
         AnotherScreenKey(1),
         AnotherScreenKey(2),
         LoginScreenKey(OpenScreen(AnotherScreenKey(3)))
      )

      initViewModel()

      viewModel.logout()
      advanceTimeBy(1000)

      navigator.backstack.shouldContainExactly(
         LoginScreenKey(MultiNavigationInstructions(OpenScreen(HomeScreenKey), OpenScreen(PostListScreenKey)))
      )
   }

   private fun initViewModel() {
      viewModel.onServiceRegistered()
   }

   @Parcelize
   private data class AnotherScreenKey(val number: Int = 0) : ScreenKey()
}
