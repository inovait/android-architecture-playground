package si.inova.androidarchitectureplayground.user.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import si.inova.androidarchitectureplayground.user.UsersDataProviders
import si.inova.androidarchitectureplayground.user.sqldelight.generated.Database
import si.inova.androidarchitectureplayground.user.sqldelight.generated.DbUser
import si.inova.androidarchitectureplayground.user.sqldelight.generated.DbUserQueries

fun createTestUserQueries(): DbUserQueries {
   val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
   Database.Schema.create(driver)

   return UsersDataProviders.createUserQueries(driver)
}

fun DbUserQueries.insertSampleUsers() {
   transaction {
      insert(
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

      insert(
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
   }
}
