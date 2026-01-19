package si.inova.androidarchitectureplayground.user

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.map
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.paging3.QueryPagingSource
import dev.zacsweers.metro.Inject
import dispatch.core.IOCoroutineScope
import dispatch.core.dispatcherProvider
import dispatch.core.ioDispatcher
import dispatch.core.withIO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import si.inova.androidarchitectureplayground.network.exceptions.BackendException
import si.inova.androidarchitectureplayground.user.exceptions.UnknownUserException
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.androidarchitectureplayground.user.model.toDb
import si.inova.androidarchitectureplayground.user.model.toUser
import si.inova.androidarchitectureplayground.user.network.UsersService
import si.inova.androidarchitectureplayground.user.sqldelight.generated.DbUser
import si.inova.androidarchitectureplayground.user.sqldelight.generated.DbUserQueries
import si.inova.kotlinova.core.exceptions.UnknownCauseException
import si.inova.kotlinova.core.outcome.CauseException
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.time.TimeProvider
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.coroutineContext

// @ContributesBinding(AppScope::class)
@Inject
class UserRepositoryImpl(
   private val usersService: UsersService,
   private val userDb: DbUserQueries,
   private val timeProvider: TimeProvider,
   private val ioScope: IOCoroutineScope,
) : UserRepository {
   @OptIn(ExperimentalPagingApi::class)
   override fun getAllUsers(): Flow<PagingData<User>> {
      return Pager(
         PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
         pagingSourceFactory = {
            QueryPagingSource(
               countQuery = userDb.countUsers(),
               transacter = userDb,
               context = ioScope.ioDispatcher,
               queryProvider = userDb::selectAll
            )
         },
         remoteMediator = mediator
      )
         .flow
         .map { pagingData ->
            pagingData.map {
               it.toUser()
            }
         }
   }

   override fun getUserDetails(id: Int, force: Boolean): Flow<Outcome<User>> {
      val dbQuery = userDb.selectSingle(id.toLong())

      return suspend { withIO { dbQuery.awaitAsOneOrNull() } }
         .asFlow()
         .flatMapLatest { initialDbUser ->
            flow {
               val deadline = timeProvider.currentTimeMillis() - CACHE_DURATION_MS
               val initialUser = initialDbUser?.toUser()

               if (!loadSingleUserFromNetwork(force, initialDbUser, deadline, initialUser, id)) return@flow

               emitAll(
                  dbQuery.asFlow().mapToOne(coroutineContext.dispatcherProvider.io).map { Outcome.Success(it.toUser()) }
               )
            }
         }
   }

   @Suppress("CognitiveComplexMethod") // Lots of catches, but otherwise it's okay
   private suspend fun FlowCollector<Outcome<User>>.loadSingleUserFromNetwork(
      force: Boolean,
      initialDbUser: DbUser?,
      deadline: Long,
      initialUser: User?,
      id: Int,
   ): Boolean {
      if (force || initialDbUser == null || !initialDbUser.isValidForUserDetails(deadline)) {
         if (initialDbUser != null) {
            emit(Outcome.Progress(initialUser))
         }

         try {
            val dbUser = usersService.getUser(id).toDb(timeProvider.currentTimeMillis())
            withIO {
               userDb.insert(dbUser)
            }
         } catch (e: BackendException) {
            val error = if (e.backendMessage.contains("not found")) {
               UnknownUserException(e)
            } else {
               UnknownCauseException(cause = e)
            }

            emit(Outcome.Error(error, initialUser))
            return false
         } catch (e: CancellationException) {
            throw e
         } catch (e: CauseException) {
            emit(Outcome.Error(e, initialUser))
            return false
         } catch (e: Exception) {
            emit(Outcome.Error(UnknownCauseException(cause = e), initialUser))
            return false
         }
      }
      return true
   }

   @OptIn(ExperimentalPagingApi::class)
   private val mediator = object : RemoteMediator<Int, DbUser>() {
      override suspend fun load(loadType: LoadType, state: PagingState<Int, DbUser>): MediatorResult {
         return try {
            val loadKey = when (loadType) {
               LoadType.REFRESH -> 0
               LoadType.PREPEND ->
                  // Prepend is not supported
                  return MediatorResult.Success(endOfPaginationReached = true)

               // IDs are one-based, so we can just return ID of the last item to get the index of the first next item
               LoadType.APPEND -> state.lastItemOrNull()?.id?.toInt() ?: 0
            }

            val loadSize = if (loadKey == 0) state.config.initialLoadSize else state.config.pageSize

            val data = usersService.getUsers(loadSize, loadKey).users.map { it.toUser() }

            withIO {
               saveUsersToDatabase(data, replaceExisting = loadType == LoadType.REFRESH)
            }

            MediatorResult.Success(endOfPaginationReached = data.isEmpty() || data.size < loadSize)
         } catch (e: CancellationException) {
            throw e
         } catch (e: Exception) {
            MediatorResult.Error(e)
         }
      }

      override suspend fun initialize(): InitializeAction {
         val deadline = timeProvider.currentTimeMillis() - CACHE_DURATION_MS
         val oldestEntry = withIO {
            userDb.selectOldestLastUpdate().executeAsOneOrNull()?.min
         }

         return if (oldestEntry == null || oldestEntry < deadline) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
         } else {
            InitializeAction.SKIP_INITIAL_REFRESH
         }
      }
   }

   private fun saveUsersToDatabase(
      data: List<User>,
      replaceExisting: Boolean,
   ) {
      val dbUsers = data.map { it.toDb(fullData = false, lastUpdate = timeProvider.currentTimeMillis()) }
      if (replaceExisting) {
         userDb.replaceAll(dbUsers)
      } else {
         userDb.insert(dbUsers)
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

private val CACHE_DURATION_MS = TimeUnit.MINUTES.toMillis(10L)
private const val DEFAULT_PAGE_SIZE = 10
