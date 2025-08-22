package si.inova.androidarchitectureplayground.post

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dispatch.core.dispatcherProvider
import dispatch.core.withIO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import si.inova.androidarchitectureplayground.common.pagination.OffsetDatabaseBackedPaginatedDataStream
import si.inova.androidarchitectureplayground.common.pagination.PaginatedDataStream
import si.inova.androidarchitectureplayground.network.exceptions.BackendException
import si.inova.androidarchitectureplayground.post.exceptions.UnknownPostException
import si.inova.androidarchitectureplayground.post.model.Post
import si.inova.androidarchitectureplayground.post.model.toDb
import si.inova.androidarchitectureplayground.post.model.toPost
import si.inova.androidarchitectureplayground.post.network.PostsService
import si.inova.androidarchitectureplayground.post.sqldelight.generated.DbPost
import si.inova.androidarchitectureplayground.post.sqldelight.generated.DbPostQueries
import si.inova.kotlinova.core.exceptions.UnknownCauseException
import si.inova.kotlinova.core.outcome.CauseException
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.outcome.catchIntoOutcome
import si.inova.kotlinova.core.time.TimeProvider
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

@ContributesBinding(AppScope::class)
class PostsRepositoryImpl @Inject constructor(
   private val postsService: PostsService,
   private val postDb: DbPostQueries,
   private val timeProvider: TimeProvider,
) : PostsRepository {
   override fun getAllPosts(force: Boolean): PaginatedDataStream<List<Post>> {
      return OffsetDatabaseBackedPaginatedDataStream<Post>(
         loadFromNetwork = ::loadPostsFromNetwork,
         loadFromDatabase = { offset, limit -> loadPostsFromDatabase(offset, limit, force) },
         saveToDatabase = ::savePostsToDatabase
      )
   }

   override fun getPostDetails(id: Int, force: Boolean): Flow<Outcome<Post>> {
      val dbQuery = postDb.selectSingle(id.toLong())

      return suspend { withIO { dbQuery.awaitAsOneOrNull() } }
         .asFlow()
         .flatMapLatest { initialDbPost ->
            flow {
               val deadline = timeProvider.currentTimeMillis() - CACHE_DURATION_MS
               val initialPost = initialDbPost?.toPost()

               if (!loadFromNetwork(force, initialDbPost, deadline, initialPost, id)) return@flow

               emitAll(
                  dbQuery.asFlow().mapToOne(coroutineContext.dispatcherProvider.io).map { Outcome.Success(it.toPost()) }
               )
            }
         }
   }

   private suspend fun FlowCollector<Outcome<Post>>.loadFromNetwork(
      force: Boolean,
      initialDbPost: DbPost?,
      deadline: Long,
      initialPost: Post?,
      id: Int,
   ): Boolean {
      if (force || initialDbPost == null || !initialDbPost.isValidForPostDetails(deadline)) {
         if (initialDbPost != null) {
            emit(Outcome.Progress(initialPost))
         }

         try {
            val dbPost = postsService.getPost(id).toDb(timeProvider.currentTimeMillis())
            withIO {
               postDb.insert(dbPost)
            }
         } catch (e: BackendException) {
            val error = if (e.backendMessage.contains("not found")) {
               UnknownPostException("Unknown post $id", e)
            } else {
               UnknownCauseException(cause = e)
            }

            emit(Outcome.Error(error, initialPost))
            return false
         } catch (e: CauseException) {
            emit(Outcome.Error(e, initialPost))
            return false
         } catch (e: Exception) {
            emit(Outcome.Error(UnknownCauseException(cause = e), initialPost))
            return false
         }
      }
      return true
   }

   private suspend fun loadPostsFromNetwork(
      offset: Int,
      limit: Int,
   ) = catchIntoOutcome {
      Outcome.Success(postsService.getPosts(limit, offset).posts.map { it.toPost() })
   }

   private fun savePostsToDatabase(
      data: List<Post>,
      replaceExisting: Boolean,
   ) {
      val dbPosts = data.map { it.toDb(fullData = false, lastUpdate = timeProvider.currentTimeMillis()) }
      if (replaceExisting) {
         postDb.replaceAll(dbPosts)
      } else {
         postDb.insert(dbPosts)
      }
   }

   private fun loadPostsFromDatabase(
      offset: Int,
      limit: Int,
      force: Boolean,
   ): Flow<Outcome.Success<OffsetDatabaseBackedPaginatedDataStream.DatabaseResult<Post>>> {
      val deadline = timeProvider.currentTimeMillis() - CACHE_DURATION_MS

      return postDb
         .selectAll(offset = offset.toLong(), limit = limit.toLong())
         .asFlow()
         .map { query ->
            val dbPosts = withIO { query.awaitAsList() }
            val expired = dbPosts.any { it.last_update < deadline }

            Outcome.Success(
               OffsetDatabaseBackedPaginatedDataStream.DatabaseResult(
                  dbPosts.map { it.toPost() },
                  !expired && !force
               )
            )
         }
   }
}

private fun DbPostQueries.replaceAll(newPosts: List<DbPost>) {
   transaction {
      clear()

      for (post in newPosts) {
         insert(post)
      }
   }
}

private fun DbPostQueries.insert(newPosts: List<DbPost>) {
   transaction {
      for (post in newPosts) {
         insert(post)
      }
   }
}

private fun DbPost.isValidForPostDetails(cacheDeadline: Long): Boolean {
   return last_update > cacheDeadline && full_data == true
}

private val CACHE_DURATION_MS = TimeUnit.MINUTES.toMillis(10L)
