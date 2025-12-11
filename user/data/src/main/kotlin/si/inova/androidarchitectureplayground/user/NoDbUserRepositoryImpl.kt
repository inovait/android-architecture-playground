package si.inova.androidarchitectureplayground.user

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import si.inova.androidarchitectureplayground.paging.StaleWhileRevalidateInputPagingWrapper
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.androidarchitectureplayground.user.model.toDb
import si.inova.androidarchitectureplayground.user.model.toUser
import si.inova.androidarchitectureplayground.user.network.UsersService
import si.inova.androidarchitectureplayground.user.network.model.LightUserDto
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.outcome.mapData

/**
 * Example of using Paging + Stale while revalidate retrofit for lists
 */
@ContributesBinding(AppScope::class)
@Inject
class NoDbUserRepositoryImpl(
   private val usersService: UsersService,
) : UserRepository {
   @OptIn(ExperimentalPagingApi::class)
   override fun getAllUsers(): Flow<PagingData<User>> {
      return StaleWhileRevalidateInputPagingWrapper<LightUserDto>(
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
      ).flow
         .map { pagingData ->
            pagingData.map {
               it.toUser()
            }
         }
   }

   override fun getUserDetails(id: Int, force: Boolean): Flow<Outcome<User>> {
      return usersService.getUserStaleWhileRevalidate(id, force).map { outcome ->
         outcome.mapData { it.toDb(0L).toUser() }
      }
   }
}

private const val DEFAULT_PAGE_SIZE = 10
