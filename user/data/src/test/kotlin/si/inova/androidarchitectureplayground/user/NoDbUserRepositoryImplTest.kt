package si.inova.androidarchitectureplayground.user

import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.common.test.datastore.runTestWithDispatchers
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.androidarchitectureplayground.user.network.FakeUsersService
import si.inova.androidarchitectureplayground.user.network.model.LightUserDto
import si.inova.androidarchitectureplayground.user.network.model.UserDto
import si.inova.kotlinova.core.test.TestScopeWithDispatcherProvider
import si.inova.kotlinova.core.test.outcomes.shouldBeSuccessWithData

class NoDbUserRepositoryImplTest {
   private val userService = FakeUsersService()
   private val scope = TestScopeWithDispatcherProvider()

   private val repository = NoDbUserRepositoryImpl(
      userService,
   )

   @BeforeEach
   fun setUp() {
      userService.providedUsers = createFakeUsersDto(0, 60)
   }

   @Test
   fun `Provide first page of list of users from network`() = scope.runTestWithDispatchers {
      val expectedUsers = createFakeUsers(0, 30)

      val dataStream = repository.getAllUsers()
      val actualUsers = dataStream.asSnapshot {}

      actualUsers shouldBe expectedUsers
   }

   @Test
   fun `Provide second page of list of users from network`() = scope.runTestWithDispatchers {
      val dataStream = repository.getAllUsers()
      val actualUsers = dataStream.asSnapshot {
         scrollTo(20)
      }

      // Prefetching logic in Paging prevents us from easily determining how much is loaded, so just test that
      // later items are actually loaded
      actualUsers.last().id shouldBeGreaterThan 30
   }

   @Test
   fun `Provide single user from network`() = scope.runTestWithDispatchers {
      userService.provideUserDetails(
         UserDto(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            maidenName = "Smith",
            age = 25,
            gender = "Apache Helicopter",
            email = "a@b.com",
            phone = "1234567890",
            hair = UserDto.Hair(
               "brown",
               "curly"
            ),
         )
      )

      val expectedUser = User(
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

      val user = repository.getUserDetails(1)
      user.test {
         awaitItem() shouldBeSuccessWithData expectedUser
         cancelAndConsumeRemainingEvents()
      }
   }

   private fun createFakeUsers(from: Int, to: Int): List<User> {
      return List(to - from) { index ->
         User(
            id = index + 1,
            firstName = "First $index",
            lastName = "Second $index"
         )
      }
   }
}

private fun createFakeUsersDto(from: Int, to: Int): List<LightUserDto> {
   return List(to - from) { index ->
      LightUserDto(
         index + 1,
         "First $index",
         "Second $index"
      )
   }
}
