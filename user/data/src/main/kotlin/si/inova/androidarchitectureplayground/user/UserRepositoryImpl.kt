package si.inova.androidarchitectureplayground.user

import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.squareup.anvil.annotations.ContributesBinding
import dispatch.core.dispatcherProvider
import dispatch.core.withIO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import si.inova.androidarchitectureplayground.common.di.ApplicationScope
import si.inova.androidarchitectureplayground.location.LocationRetriever
import si.inova.androidarchitectureplayground.location.model.Location
import si.inova.androidarchitectureplayground.network.exceptions.BackendException
import si.inova.androidarchitectureplayground.user.exceptions.UnknownUserException
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.androidarchitectureplayground.user.model.toDb
import si.inova.androidarchitectureplayground.user.model.toUser
import si.inova.androidarchitectureplayground.user.network.UsersService
import si.inova.androidarchitectureplayground.user.sqldelight.generated.DbUser
import si.inova.androidarchitectureplayground.user.sqldelight.generated.DbUserQueries
import si.inova.kotlinova.core.exceptions.UnknownCauseException
import si.inova.kotlinova.core.logging.logcat
import si.inova.kotlinova.core.outcome.CauseException
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.outcome.catchIntoOutcome
import si.inova.kotlinova.core.outcome.mapData
import si.inova.kotlinova.core.time.TimeProvider
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@ContributesBinding(ApplicationScope::class)
class UserRepositoryImpl @Inject constructor(
   private val usersService: UsersService,
   private val userDb: DbUserQueries,
   private val timeProvider: TimeProvider,
   private val locationRetriever: LocationRetriever
) : UserRepository {
   override fun getAllUsers(force: Boolean): Flow<Outcome<List<User>>> {
      return locationRetriever.getLocation()
         .map { location ->
            logcat { "Got location $location" }
            loadUsersAndSortByDistance(location)
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
                     return@flow
                  } catch (e: CauseException) {
                     emit(Outcome.Error(e, initialUser))
                     return@flow
                  } catch (e: Exception) {
                     emit(Outcome.Error(UnknownCauseException(cause = e), initialUser))
                     return@flow
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

   @Suppress("ALL")
   private suspend fun UserRepositoryImpl.loadUsersAndSortByDistance(location: Location) =
      loadUsersFromNetwork(0, 30).mapData { users ->
         // TODO sort by location
         users
      }
}

private fun DbUser.isValidForUserDetails(cacheDeadline: Long): Boolean {
   return last_update > cacheDeadline && full_data == true
}

private val CACHE_DURATION_MS = TimeUnit.MINUTES.toMillis(10L)
