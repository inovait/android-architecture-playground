package si.inova.androidarchitectureplayground.post.db

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.post.sqldelight.generated.DbPost

class PostsDatabaseTest {
   private val postQueries = createTestPostQueries()

   @BeforeEach
   fun setUp() {
      postQueries.insertSamplePosts()
   }

   @Test
   fun `Load post list`() {
      val posts = postQueries.selectAll(limit = Long.MAX_VALUE, offset = 0L).executeAsList()

      val expectedPosts = listOf(
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
         ),
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
         ),
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
         ),
      )

      posts shouldBe expectedPosts
   }

   @Test
   fun `Load post list with limit`() {
      val posts = postQueries.selectAll(limit = 1L, offset = 0L).executeAsList()

      val expectedPosts = listOf(
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

      posts shouldBe expectedPosts
   }

   @Test
   fun `Load post list with offset`() {
      val posts = postQueries.selectAll(limit = Long.MAX_VALUE, offset = 1L).executeAsList()

      val expectedPosts = listOf(
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
         ),
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
         ),
      )

      posts shouldBe expectedPosts
   }

   @Test
   fun `Load single post`() {
      val post = postQueries.selectSingle(12).executeAsOneOrNull()

      val expectedPost = DbPost(
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

      post shouldBe expectedPost
   }

   @Test
   fun `Clear table`() {
      postQueries.clear()

      val posts = postQueries.selectAll(limit = Long.MAX_VALUE, offset = 0L).executeAsList()
      posts shouldBe emptyList()
   }
}
