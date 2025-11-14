package si.inova.androidarchitectureplayground.user

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dispatch.core.IOCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.Cache
import si.inova.androidarchitectureplayground.paging.staleWhileRevalidateInputPagingWrapper
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.androidarchitectureplayground.user.model.toUser
import si.inova.androidarchitectureplayground.user.network.UsersService
import si.inova.androidarchitectureplayground.user.network.model.LightUserDto
import si.inova.androidarchitectureplayground.user.network.model.UsersDto
import si.inova.androidarchitectureplayground.user.sqldelight.generated.DbUser
import si.inova.androidarchitectureplayground.user.sqldelight.generated.DbUserQueries
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.outcome.mapData
import si.inova.kotlinova.core.time.TimeProvider
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

/**
 * Example of using Paging + Stale while revalidate retrofit for lists
 */
@ContributesBinding(AppScope::class)
class NoDbUserRepositoryImpl @Inject constructor(
   private val usersService: UsersService,
   private val timeProvider: TimeProvider,
   private val ioScope: IOCoroutineScope,
   private val cache: Lazy<Cache>,
) : UserRepository {
   @OptIn(ExperimentalPagingApi::class)
   override fun getAllUsers(): Flow<PagingData<User>> {
      return staleWhileRevalidateInputPagingWrapper<LightUserDto>(
         pager = { pagingSourceFactory, remoteMediator ->
            Pager(
               PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
               pagingSourceFactory = pagingSourceFactory,
               remoteMediator = remoteMediator
            )
         },
         getFirstPage = { force, pageSize ->
            usersService.getUsersStaleWhileRevalidate(limit = pageSize, force = force)
               .map { outcome -> outcome.mapData { it.users } }
         },
         getSubsequentPage = { itemsLoadedSoFar, pageSize ->
            usersService.getUsers(skip = itemsLoadedSoFar, limit = pageSize).users
         },
      )
         .map { pagingData ->
            pagingData.map {
               it.toUser()
            }
         }
   }

   private fun getUsersStaleWhileRevalidate(
      limit: Int,
      skip: Int,
      force: Boolean,
   ): Flow<Outcome<UsersDto>> {
      val users = List(limit) {
         LightUserDto(
            id = skip + it + 1,
            firstName = "First $it",
            lastName = "Second $it"
         )
      }

      return flow {
         emit(Outcome.Progress(UsersDto(users, total = 0)))

         delay(2.seconds)

         emit(Outcome.Success(UsersDto(users.shuffled(), total = 0)))
      }
   }

   override fun getUserDetails(id: Int, force: Boolean): Flow<Outcome<User>> {
      TODO()
      // val dbQuery = userDb.selectSingle(id.toLong())
      //
      // return suspend { withIO { dbQuery.awaitAsOneOrNull() } }
      //    .asFlow()
      //    .flatMapLatest { initialDbUser ->
      //       flow {
      //          val deadline = timeProvider.currentTimeMillis() - CACHE_DURATION_MS
      //          val initialUser = initialDbUser?.toUser()
      //
      //          if (!loadSingleUserFromNetwork(force, initialDbUser, deadline, initialUser, id)) return@flow
      //
      //          emitAll(
      //             dbQuery.asFlow().mapToOne(coroutineContext.dispatcherProvider.io).map { Outcome.Success(it.toUser()) }
      //          )
      //       }
      //    }
   }

   // private suspend fun FlowCollector<Outcome<User>>.loadSingleUserFromNetwork(
   //    force: Boolean,
   //    initialDbUser: DbUser?,
   //    deadline: Long,
   //    initialUser: User?,
   //    id: Int,
   // ): Boolean {
   //    if (force || initialDbUser == null || !initialDbUser.isValidForUserDetails(deadline)) {
   //       if (initialDbUser != null) {
   //          emit(Outcome.Progress(initialUser))
   //       }
   //
   //       try {
   //          val dbUser = usersService.getUser(id).toDb(timeProvider.currentTimeMillis())
   //          withIO {
   //             userDb.insert(dbUser)
   //          }
   //       } catch (e: BackendException) {
   //          val error = if (e.backendMessage.contains("not found")) {
   //             UnknownUserException(e)
   //          } else {
   //             UnknownCauseException(cause = e)
   //          }
   //
   //          emit(Outcome.Error(error, initialUser))
   //          return false
   //       } catch (e: CauseException) {
   //          emit(Outcome.Error(e, initialUser))
   //          return false
   //       } catch (e: Exception) {
   //          emit(Outcome.Error(UnknownCauseException(cause = e), initialUser))
   //          return false
   //       }
   //    }
   //    return true
   // }

   inner class UserListPagingSource : PagingSource<Int, DbUser>() {

      override fun getRefreshKey(state: PagingState<Int, DbUser>): Int? {
         return 0
      }

      override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DbUser> {
         this.invalidate()

         TODO("Not yet implemented")
      }
   }
}

private fun DbUserQueries.replaceAll(newUsers: List<DbUser>) {
   transaction {
      clear()

      for (user in newUsers) {
         insert(user)
      }
   }
}

private fun DbUserQueries.insert(newUsers: List<DbUser>) {
   transaction {
      for (user in newUsers) {
         insert(user)
      }
   }
}

private fun DbUser.isValidForUserDetails(cacheDeadline: Long): Boolean {
   return last_update > cacheDeadline && full_data == true
}

private const val DEFAULT_PAGE_SIZE = 10
