package si.inova.androidarchitectureplayground.post.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import si.inova.androidarchitectureplayground.post.PostsDataModule
import si.inova.androidarchitectureplayground.post.sqldelight.generated.Database
import si.inova.androidarchitectureplayground.post.sqldelight.generated.DbPost
import si.inova.androidarchitectureplayground.post.sqldelight.generated.DbPostQueries

fun createTestPostQueries(): DbPostQueries {
   val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
   Database.Schema.create(driver)

   return PostsDataModule.providePostQueries(driver)
}

fun DbPostQueries.insertSamplePosts() {
   transaction {
      insert(
         DbPost(
            id = 12,
            title = "She was aware that things could go wrong.",
            body = "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
            user_id = 26,
            tags = "love,english",
            image_url = "https://i.dummyjson.com/data/products/12/1.jpg",
            full_data = false,
            last_update = 0L,
            num_reactions = 7
         )
      )

      insert(
         DbPost(
            id = 1,
            title = "His mother had always taught him",
            body = null,
            user_id = null,
            tags = null,
            image_url = null,
            num_reactions = null,
            full_data = false,
            last_update = 0L,
         )
      )

      insert(
         DbPost(
            id = 2,
            title = "He was an expert but not in a discipline",
            body = null,
            user_id = null,
            tags = null,
            image_url = null,
            num_reactions = null,
            full_data = false,
            last_update = 0L,
         )
      )
   }
}
