package si.inova.androidarchitectureplayground.user.network

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.network.exceptions.BackendException
import si.inova.androidarchitectureplayground.network.test.serviceFactory
import si.inova.androidarchitectureplayground.user.UsersDataComponent
import si.inova.androidarchitectureplayground.user.network.model.LightUserDto
import si.inova.androidarchitectureplayground.user.network.model.UserDto
import si.inova.androidarchitectureplayground.user.network.model.UsersDto
import si.inova.kotlinova.retrofit.mockWebServer
import si.inova.kotlinova.retrofit.setJsonBodyFromResource

class UsersServiceTest {
   @Test
   fun `Load user list`() = runTest {
      mockWebServer {
         mockResponse("/users") {
            setJsonBodyFromResource("user_list.json")
         }

         val serviceFactory = serviceFactory(this@runTest)
         val service = object : UsersDataComponent {}.provideUsersService(serviceFactory)

         val expectedUsers = UsersDto(
            listOf(
               LightUserDto(
                  1,
                  "Terry",
                  "Medhurst"
               ),
               LightUserDto(
                  2,
                  "Juliet",
                  "Beck"
               ),
               LightUserDto(
                  3,
                  "Max",
                  "Howell"
               ),
            ),
            total = 10
         )

         val users = service.getUsers()

         users shouldBe expectedUsers
      }
   }

   @Test
   fun `Load user list with limit and skip`() = runTest {
      mockWebServer {
         mockResponse("/users?limit=3&skip=2") {
            setJsonBodyFromResource("user_list.json")
         }

         val serviceFactory = serviceFactory(this@runTest)
         val service = object : UsersDataComponent {}.provideUsersService(serviceFactory)

         val expectedUsers = UsersDto(
            listOf(
               LightUserDto(
                  1,
                  "Terry",
                  "Medhurst"
               ),
               LightUserDto(
                  2,
                  "Juliet",
                  "Beck"
               ),
               LightUserDto(
                  3,
                  "Max",
                  "Howell"
               ),
            ),
            total = 10
         )

         val users = service.getUsers(limit = 3, skip = 2)

         users shouldBe expectedUsers
      }
   }

   @Test
   fun `Load single user`() = runTest {
      mockWebServer {
         mockResponse("/users/7") {
            setJsonBodyFromResource("single_user.json")
         }

         val serviceFactory = serviceFactory(this@runTest)
         val service = object : UsersDataComponent {}.provideUsersService(serviceFactory)

         val expectedUser = UserDto(
            id = 7,
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

         val user = service.getUser(7)

         user shouldBe expectedUser
      }
   }

   @Test
   fun `Report proper exception when user does not exist`() = runTest {
      mockWebServer {
         mockResponse("/users/7") {
            setJsonBodyFromResource("no_user_error.json")
            setResponseCode(404)
         }

         val serviceFactory = serviceFactory(this@runTest)
         val service = object : UsersDataComponent {}.provideUsersService(serviceFactory)

         shouldThrow<BackendException> {
            service.getUser(7)
         }.message shouldBe "User with id '7' not found"
      }
   }
}
