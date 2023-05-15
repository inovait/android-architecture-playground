package si.inova.androidarchitectureplayground.user.db

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.user.sqldelight.generated.DbUser

class UsersDatabaseTest {
   private val userQueries = createTestUserQueries()

   @BeforeEach
   fun setUp() {
      userQueries.insertSampleUsers()
   }

   @Test
   fun `Load user list`() {
      val users = userQueries.selectAll(limit = Long.MAX_VALUE, offset = 0L).executeAsList()

      val expectedUsers = listOf(
         DbUser(
            id = 2,
            first_name = "Juliet",
            last_name = "Beck",
            full_data = true,
            last_update = 2L,
            age = null,
            email = null,
            gender = null,
            hair_color = null,
            hair_type = null,
            maiden_name = null,
            phone = null,
         ),
         DbUser(
            id = 7,
            first_name = "John",
            last_name = "Doe",
            maiden_name = "Smith",
            age = 25,
            gender = "Apache Helicopter",
            email = "a@b.com",
            phone = "1234567890",
            hair_color = "brown",
            hair_type = "curly",
            full_data = true,
            last_update = 1L,
         )
      )

      users shouldBe expectedUsers
   }

   @Test
   fun `Load user list with limit`() {
      val users = userQueries.selectAll(limit = 1L, offset = 0L).executeAsList()

      val expectedUsers = listOf(
         DbUser(
            id = 2,
            first_name = "Juliet",
            last_name = "Beck",
            full_data = true,
            last_update = 2L,
            age = null,
            email = null,
            gender = null,
            hair_color = null,
            hair_type = null,
            maiden_name = null,
            phone = null,
         )
      )

      users shouldBe expectedUsers
   }

   @Test
   fun `Load user list with offset`() {
      val users = userQueries.selectAll(limit = Long.MAX_VALUE, offset = 1L).executeAsList()

      val expectedUsers = listOf(
         DbUser(
            id = 7,
            first_name = "John",
            last_name = "Doe",
            maiden_name = "Smith",
            age = 25,
            gender = "Apache Helicopter",
            email = "a@b.com",
            phone = "1234567890",
            hair_color = "brown",
            hair_type = "curly",
            full_data = true,
            last_update = 1L,
         )
      )

      users shouldBe expectedUsers
   }

   @Test
   fun `Load single user`() {
      val user = userQueries.selectSingle(7).executeAsOneOrNull()

      val expectedUser = DbUser(
         id = 7,
         first_name = "John",
         last_name = "Doe",
         maiden_name = "Smith",
         age = 25,
         gender = "Apache Helicopter",
         email = "a@b.com",
         phone = "1234567890",
         hair_color = "brown",
         hair_type = "curly",
         full_data = true,
         last_update = 1L,
      )

      user shouldBe expectedUser
   }

   @Test
   fun `Clear table`() {
      userQueries.clear()

      val users = userQueries.selectAll(limit = Long.MAX_VALUE, offset = 0L).executeAsList()
      users shouldBe emptyList()
   }
}
