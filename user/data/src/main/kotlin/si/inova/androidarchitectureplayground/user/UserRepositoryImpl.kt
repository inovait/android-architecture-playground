package si.inova.androidarchitectureplayground.user

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import dispatch.core.dispatcherProvider
import dispatch.core.withIO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import si.inova.androidarchitectureplayground.common.pagination.OffsetDatabaseBackedPaginatedDataStream
import si.inova.androidarchitectureplayground.common.pagination.PaginatedDataStream
import si.inova.androidarchitectureplayground.network.exceptions.BackendException
import si.inova.androidarchitectureplayground.user.exceptions.UnknownUserException
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.androidarchitectureplayground.user.model.toDb
import si.inova.androidarchitectureplayground.user.model.toUser
import si.inova.androidarchitectureplayground.user.network.UsersService
import si.inova.androidarchitectureplayground.user.sqldelight.generated.DbUser
import si.inova.androidarchitectureplayground.user.sqldelight.generated.DbUserQueries
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.outcome.catchIntoOutcome
import si.inova.kotlinova.core.time.TimeProvider
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class UserRepositoryImpl @Inject constructor(
   private val usersService: UsersService,
   private val userDb: DbUserQueries,
   private val timeProvider: TimeProvider
) : UserRepository {
   override fun getAllUsers(force: Boolean): PaginatedDataStream<List<User>> {
      return OffsetDatabaseBackedPaginatedDataStream<User>(
         loadFromNetwork = ::loadUsersFromNetwork,
         loadFromDatabase = { offset, limit -> loadUsersFromDatabase(offset, limit, force) },
         saveToDatabase = ::saveUsersToDatabase
      )
   }

   override fun getUserDetails(id: Int, force: Boolean): Flow<Outcome<User>> {
      val dbQuery = userDb.selectSingle(id.toLong())

      return suspend { withIO { dbQuery.awaitAsOneOrNull() } }
         .asFlow()
         .flatMapLatest { initialDbUser ->
            flow {
               val deadline = timeProvider.currentTimeMillis() - CACHE_DURATION_MS

               if (force || initialDbUser == null || !initialDbUser.isValidForUserDetails(deadline)) {
                  if (initialDbUser != null) {
                     emit(Outcome.Progress(initialDbUser.toUser()))
                  }

                  try {
                     val dbUser = usersService.getUser(id).toDb(timeProvider.currentTimeMillis())
                     userDb.insert(dbUser)
                  } catch (e: BackendException) {
                     if (e.backendMessage.contains("not found")) {
                        emit(Outcome.Error(UnknownUserException(e)))
                        return@flow
                     } else {
                        throw e
                     }
                  }
               }

               emitAll(
                  dbQuery.asFlow().mapToOne(coroutineContext.dispatcherProvider.io).map { Outcome.Success(it.toUser()) }
               )
            }
         }
   }

   private suspend fun loadUsersFromNetwork(
      offset: Int,
      limit: Int
   ) = catchIntoOutcome {
      Outcome.Success(usersService.getUsers(limit, offset).users.map { it.toUser() })
   }

   private fun saveUsersToDatabase(
      data: List<User>,
      replaceExisting: Boolean
   ) {
      val dbUsers = data.map { it.toDb(fullData = false, lastUpdate = timeProvider.currentTimeMillis()) }
      if (replaceExisting) {
         userDb.replaceAll(dbUsers)
      } else {
         userDb.insert(dbUsers)
      }
   }

   private fun loadUsersFromDatabase(
      offset: Int,
      limit: Int,
      force: Boolean
   ): Flow<Outcome.Success<OffsetDatabaseBackedPaginatedDataStream.DatabaseResult<User>>> {
      val deadline = timeProvider.currentTimeMillis() - CACHE_DURATION_MS

      return userDb
         .selectAll(offset = offset.toLong(), limit = limit.toLong())
         .asFlow()
         .map { query ->
            val dbUsers = query.awaitAsList()
            val expired = dbUsers.any { it.last_update < deadline }

            Outcome.Success(
               OffsetDatabaseBackedPaginatedDataStream.DatabaseResult(
                  dbUsers.map { it.toUser() },
                  !expired && !force
               )
            )
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
