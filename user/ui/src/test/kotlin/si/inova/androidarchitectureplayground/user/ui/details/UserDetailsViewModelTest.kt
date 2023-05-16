package si.inova.androidarchitectureplayground.user.ui.details

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.user.FakeUserRepository
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.test.outcomes.shouldBeSuccessWithData
import si.inova.kotlinova.core.test.outcomes.testCoroutineResourceManager

class UserDetailsViewModelTest {
   private val scope = TestScope()
   private val userRepository = FakeUserRepository()

   private val viewModel = UserDetailsViewModel(scope.testCoroutineResourceManager(), userRepository)

   @BeforeEach
   fun setUp() {
      userRepository.setUserDetails(17, Outcome.Success(TEST_USER))
   }

   @Test
   fun `Load data`() = scope.runTest {
      viewModel.startLoading(17)

      viewModel.userDetails.test {
         runCurrent()

         expectMostRecentItem() shouldBeSuccessWithData TEST_USER
         userRepository.numTimesForceLoadCalled shouldBe 0
      }
   }

   @Test
   fun `Refresh data`() = scope.runTest {
      viewModel.startLoading(17)
      viewModel.onServiceRegistered()
      viewModel.refresh()

      viewModel.userDetails.test {
         runCurrent()

         userRepository.numTimesForceLoadCalled shouldBe 1
         cancelAndConsumeRemainingEvents()
      }
   }
}

private val TEST_USER = User(
   id = 1,
   firstName = "John",
   lastName = "Doe",
   maidenName = "Smith",
   age = 25,
   gender = "Apache Helicopter",
   email = "a@b.com",
   phone = "1234567890",
   hair = User.Hair(
      "brown",
      "curly"
   ),
)
