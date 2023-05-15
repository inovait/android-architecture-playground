package si.inova.androidarchitectureplayground.user

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.turbine.test
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.common.test.datastore.runTestWithDispatchers
import si.inova.androidarchitectureplayground.network.exceptions.BackendException
import si.inova.androidarchitectureplayground.user.db.createTestUserQueries
import si.inova.androidarchitectureplayground.user.exceptions.UnknownUserException
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.androidarchitectureplayground.user.network.FakeUsersService
import si.inova.androidarchitectureplayground.user.network.model.LightUserDto
import si.inova.androidarchitectureplayground.user.network.model.UserDto
import si.inova.kotlinova.core.exceptions.NoNetworkException
import si.inova.kotlinova.core.outcome.LoadingStyle
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.test.outcomes.shouldBeErrorWith
import si.inova.kotlinova.core.test.outcomes.shouldBeProgressWithData
import si.inova.kotlinova.core.test.outcomes.shouldBeSuccessWithData
import si.inova.kotlinova.core.test.time.virtualTimeProvider
import si.inova.kotlinova.retrofit.InterceptionStyle
import java.util.concurrent.TimeUnit

class UserRepositoryImplTest {
   private val userService = FakeUsersService()
   private val userQueries = createTestUserQueries()
   private val scope = TestScope()

   private val repository = UserRepositoryImpl(userService, userQueries, scope.virtualTimeProvider())

   @BeforeEach
   fun setUp() {
      userService.providedUsers = createFakeUsersDto(0, 30)
   }

   @Test
   fun `Provide first page of list of users from network`() = scope.runTestWithDispatchers {
      val expectedUsers = createFakeUsers(0, 10)

      val dataStream = repository.getAllUsers()
      dataStream.data.test {
         runCurrent()
         val users = expectMostRecentItem()

         users.items shouldBeSuccessWithData expectedUsers
      }
   }

   @Test
   fun `Provide second page of list of users from network`() = scope.runTestWithDispatchers {
      val expectedUsers = createFakeUsers(0, 20)

      val dataStream = repository.getAllUsers()
      dataStream.data.test {
         runCurrent()
         dataStream.nextPage()
         runCurrent()

         val users = expectMostRecentItem()

         users.items shouldBeSuccessWithData expectedUsers
      }
   }

   @Test
   fun `Provide first page from cached database`() = scope.runTestWithDispatchers {
      val expectedUsers = createFakeUsers(0, 10)

      val cachingDataStream = repository.getAllUsers()
      cachingDataStream.data.first() // Load data into database

      userService.interceptAllFutureCallsWith(InterceptionStyle.Error(IllegalStateException("Network should not be called now")))

      val dataStream = repository.getAllUsers()

      dataStream.data.test {
         runCurrent()

         val users = expectMostRecentItem()

         users.items shouldBeSuccessWithData expectedUsers
      }
   }

   @Test
   fun `Provide second page from cached database`() = scope.runTestWithDispatchers {
      val expectedUsers = createFakeUsers(0, 20)

      val cachingDataStream = repository.getAllUsers()
      cachingDataStream.data.test {
         runCurrent()
         cachingDataStream.nextPage()
         runCurrent()
         cancelAndConsumeRemainingEvents()
      }

      userService.interceptAllFutureCallsWith(InterceptionStyle.Error(AssertionError("Network should not be called now")))

      val dataStream = repository.getAllUsers()

      dataStream.data.test {
         runCurrent()
         dataStream.nextPage()
         runCurrent()

         val users = expectMostRecentItem()

         users.items shouldBeSuccessWithData expectedUsers
      }
   }

   @Test
   fun `Provide subsequent page from network if database is not ready`() = scope.runTestWithDispatchers {
      val expectedUsers = createFakeUsers(0, 20)

      val cachingDataStream = repository.getAllUsers()
      cachingDataStream.data.first() // Load data into database

      val dataStream = repository.getAllUsers()

      dataStream.data.test {
         runCurrent()
         dataStream.nextPage()
         runCurrent()

         val users = expectMostRecentItem()

         users.items shouldBeSuccessWithData expectedUsers
      }
   }

   @Test
   fun `Provide proper value for has any data left`() = scope.runTestWithDispatchers {
      val cachingDataStream = repository.getAllUsers()
      cachingDataStream.data.first() // Load data into database

      val dataStream = repository.getAllUsers()

      dataStream.data.test {
         runCurrent()
         expectMostRecentItem().hasAnyDataLeft.shouldBeTrue()

         dataStream.nextPage()
         runCurrent()
         expectMostRecentItem().hasAnyDataLeft.shouldBeTrue()

         dataStream.nextPage()
         runCurrent()
         expectMostRecentItem().hasAnyDataLeft.shouldBeTrue()

         dataStream.nextPage()
         runCurrent()
         expectMostRecentItem().hasAnyDataLeft.shouldBeFalse()
      }
   }

   @Test
   fun `When cache is expired, show expired data first as Loading while fetching in the background`() =
      scope.runTestWithDispatchers {
         val cachingDataStream = repository.getAllUsers()
         cachingDataStream.data.first() // Load data into database
         runCurrent()

         advanceTimeBy(TimeUnit.MINUTES.toMillis(15))

         val dataStream = repository.getAllUsers()

         dataStream.data.test {
            awaitItem().items shouldBeProgressWithData createFakeUsers(0, 10)
            awaitItem().items shouldBeSuccessWithData createFakeUsers(0, 10)
         }
      }

   @Test
   fun `When user requests force loading, show expired data first as Loading while fetching in the background`() =
      scope.runTestWithDispatchers {
         val cachingDataStream = repository.getAllUsers()
         cachingDataStream.data.first() // Load data into database
         runCurrent()

         userService.providedUsers = createFakeUsersDto(1, 31)

         val dataStream = repository.getAllUsers(force = true)

         dataStream.data.test {
            awaitItem().items shouldBeProgressWithData createFakeUsers(0, 10)
            awaitItem().items shouldBeSuccessWithData createFakeUsers(1, 11)
         }
      }

   @Test
   fun `Show loading of subsequent pages with additional data style`() =
      scope.runTestWithDispatchers {
         userService.interceptAllFutureCallsWith(InterceptionStyle.InfiniteLoad)

         val dataStream = repository.getAllUsers()

         dataStream.data.test {
            runCurrent()
            expectMostRecentItem().items shouldBe Outcome.Progress(emptyList(), style = LoadingStyle.NORMAL)

            userService.completeInfiniteLoad()
            runCurrent()
            dataStream.nextPage()
            runCurrent()

            expectMostRecentItem().items shouldBe Outcome.Progress(createFakeUsers(0, 10), style = LoadingStyle.ADDITIONAL_DATA)

            userService.completeInfiniteLoad()
            runCurrent()
            dataStream.nextPage()
            runCurrent()

            expectMostRecentItem().items shouldBe Outcome.Progress(createFakeUsers(0, 20), style = LoadingStyle.ADDITIONAL_DATA)
         }
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

   @Test
   fun `Provide proper error message when user does not exist`() = scope.runTestWithDispatchers {
      userService.interceptAllFutureCallsWith(
         InterceptionStyle.Error(BackendException(backendMessage = "User with id '7' not found", cause = NoNetworkException()))
      )

      val user = repository.getUserDetails(1)
      user.test {
         awaitItem().shouldBeErrorWith(exceptionType = UnknownUserException::class.java)

         cancelAndConsumeRemainingEvents()
      }
   }

   @Test
   fun `Provide single user from cached database`() = scope.runTestWithDispatchers {
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

      repository.getUserDetails(1).first() // Load data into database

      userService.interceptAllFutureCallsWith(InterceptionStyle.Error(IllegalStateException("Network should not be called now")))

      val user = repository.getUserDetails(1)
      user.test {
         awaitItem() shouldBeSuccessWithData expectedUser
         cancelAndConsumeRemainingEvents()
      }
   }

   @Test
   fun `Update user when it changes in the database`() = scope.runTestWithDispatchers {
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
         firstName = "James",
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

      repository.getUserDetails(1).first() // Load data into database

      userService.interceptAllFutureCallsWith(InterceptionStyle.Error(IllegalStateException("Network should not be called now")))

      val user = repository.getUserDetails(1)
      user.test {
         runCurrent()

         val dbUser = userQueries.selectSingle(1).awaitAsOne().copy(first_name = "James")
         userQueries.insert(dbUser)
         runCurrent()

         expectMostRecentItem() shouldBeSuccessWithData expectedUser
         cancelAndConsumeRemainingEvents()
      }
   }

   @Test
   fun `When cache for user details is expired, return expired data first as Loading while fetching in the background`() {
      val testUser = UserDto(
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
      scope.runTestWithDispatchers {
         userService.provideUserDetails(testUser)

         val expectedUserLoading = User(
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

         val expectedUserSuccess = expectedUserLoading.copy(firstName = "James")

         repository.getUserDetails(1).test {
            // Load data into database cache
            runCurrent()
            cancelAndConsumeRemainingEvents()
         }

         userService.provideUserDetails(testUser.copy(firstName = "James"))

         advanceTimeBy(TimeUnit.MINUTES.toMillis(15))

         repository.getUserDetails(1).test {
            awaitItem() shouldBeProgressWithData expectedUserLoading
            awaitItem() shouldBeSuccessWithData expectedUserSuccess
         }
      }
   }

   @Test
   fun `When force loading, return expired data first as Loading while fetching in the background`() {
      val testUser = UserDto(
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
      scope.runTestWithDispatchers {
         userService.provideUserDetails(testUser)

         val expectedUserLoading = User(
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

         val expectedUserSuccess = expectedUserLoading.copy(firstName = "James")

         repository.getUserDetails(1).test {
            // Load data into database cache
            runCurrent()
            cancelAndConsumeRemainingEvents()
         }

         userService.provideUserDetails(testUser.copy(firstName = "James"))

         runCurrent()

         repository.getUserDetails(1, force = true).test {
            awaitItem() shouldBeProgressWithData expectedUserLoading
            awaitItem() shouldBeSuccessWithData expectedUserSuccess
         }
      }
   }

   @Test
   fun `When there is partial user data in db, return partial data first as Loading while fetching in the background`() {
      scope.runTestWithDispatchers {
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

         val expectedUserLoading = User(
            id = 1,
            firstName = "First 1",
            lastName = "Second 1"
         )

         val expectedUserSuccess = User(
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

         val cachingDataStream = repository.getAllUsers()
         cachingDataStream.data.first() // Load data into database
         runCurrent()

         repository.getUserDetails(1).test {
            awaitItem() shouldBeProgressWithData expectedUserLoading
            awaitItem() shouldBeSuccessWithData expectedUserSuccess
         }
      }
   }

   private fun createFakeUsersDto(from: Int, to: Int): List<LightUserDto> {
      return List(to - from) {
         LightUserDto(
            it,
            "First $it",
            "Second $it"
         )
      }
   }

   private fun createFakeUsers(from: Int, to: Int): List<User> {
      return List(to - from) {
         User(
            id = it,
            firstName = "First $it",
            lastName = "Second $it"
         )
      }
   }
}
