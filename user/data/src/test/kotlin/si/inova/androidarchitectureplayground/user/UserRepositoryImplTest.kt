package si.inova.androidarchitectureplayground.user

import androidx.paging.testing.asSnapshot
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.turbine.test
import dispatch.core.IOCoroutineScope
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import si.inova.androidarchitectureplayground.common.test.datastore.runTestWithDispatchers
import si.inova.androidarchitectureplayground.network.exceptions.BackendException
import si.inova.androidarchitectureplayground.user.db.createTestUserQueries
import si.inova.androidarchitectureplayground.user.exceptions.UnknownUserException
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.androidarchitectureplayground.user.network.FakeUsersService
import si.inova.androidarchitectureplayground.user.network.model.LightUserDto
import si.inova.androidarchitectureplayground.user.network.model.UserDto
import si.inova.kotlinova.core.exceptions.NoNetworkException
import si.inova.kotlinova.core.test.TestScopeWithDispatcherProvider
import si.inova.kotlinova.core.test.outcomes.shouldBeErrorWith
import si.inova.kotlinova.core.test.outcomes.shouldBeProgressWithData
import si.inova.kotlinova.core.test.outcomes.shouldBeSuccessWithData
import si.inova.kotlinova.core.test.time.virtualTimeProvider
import si.inova.kotlinova.retrofit.InterceptionStyle
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes

class UserRepositoryImplTest {
   private val userService = FakeUsersService()
   private val userQueries = createTestUserQueries()
   private val scope = TestScopeWithDispatcherProvider()

   private val repository = UserRepositoryImpl(
      usersService = userService,
      userDb = userQueries,
      timeProvider = scope.virtualTimeProvider(),
      ioScope = IOCoroutineScope(scope.coroutineContext)
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
   fun `Provide first page from cached database`() = scope.runTestWithDispatchers {
      val expectedUsers = createFakeUsers(0, 30)

      val cachingDataStream = repository.getAllUsers()
      cachingDataStream.asSnapshot { } // Load data into database

      userService.interceptAllFutureCallsWith(InterceptionStyle.Error(IllegalStateException("Network should not be called now")))

      val dataStream = repository.getAllUsers()

      val actualUsers = dataStream.asSnapshot {}

      actualUsers shouldBe expectedUsers
   }

   @Test
   fun `When cache is expired, re-load from network`() = scope.runTestWithDispatchers {
      val cachingDataStream = repository.getAllUsers()
      cachingDataStream.asSnapshot { } // Load data into database

      delay(15.minutes)

      userService.interceptAllFutureCallsWith(InterceptionStyle.Error(IllegalStateException("Network should be called now")))

      val dataStream = repository.getAllUsers()

      assertThrows<IllegalStateException>("Network should be called now") {
         dataStream.asSnapshot {}
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
            firstName = "First 0",
            lastName = "Second 0"
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
         cachingDataStream.asSnapshot {} // Load data into database
         runCurrent()

         repository.getUserDetails(1).test {
            awaitItem() shouldBeProgressWithData expectedUserLoading
            awaitItem() shouldBeSuccessWithData expectedUserSuccess
         }
      }
   }

   @Test
   fun `Include stale database data in error, when network fetch of user details fails`() =
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

         val expectedUserError = User(
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

         repository.getUserDetails(1).test {
            // Load data into database cache
            runCurrent()
            cancelAndConsumeRemainingEvents()
         }

         userService.interceptAllFutureCallsWith(InterceptionStyle.Error(NoNetworkException()))

         repository.getUserDetails(1, force = true).test {
            awaitItem() // Progress item

            awaitItem().shouldBeErrorWith(
               expectedData = expectedUserError,
               exceptionType = NoNetworkException::class.java
            )

            awaitComplete()
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
