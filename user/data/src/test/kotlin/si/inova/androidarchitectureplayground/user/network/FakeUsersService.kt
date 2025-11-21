package si.inova.androidarchitectureplayground.user.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import si.inova.androidarchitectureplayground.user.network.model.LightUserDto
import si.inova.androidarchitectureplayground.user.network.model.UserDto
import si.inova.androidarchitectureplayground.user.network.model.UsersDto
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.retrofit.FakeService
import si.inova.kotlinova.retrofit.ServiceTestingHelper

class FakeUsersService(private val serviceTestingHelper: ServiceTestingHelper = ServiceTestingHelper()) :
   UsersService, FakeService by serviceTestingHelper {
   var providedUsers: List<LightUserDto>? = null
   private val providedUserDetails: MutableMap<Int, UserDto> = HashMap()
   var lastReceivedLimitSkip: Pair<Int?, Int?>? = null

   fun provideUserDetails(details: UserDto) {
      providedUserDetails[details.id] = details
   }

   override suspend fun getUsers(limit: Int?, skip: Int?): UsersDto {
      serviceTestingHelper.intercept()
      lastReceivedLimitSkip = limit to skip
      val providedUsers = providedUsers ?: error("No users provided")
      val userList = providedUsers.drop(skip ?: 0).take(limit ?: Int.MAX_VALUE)

      return UsersDto(userList, providedUsers.size)
   }

   override suspend fun getUser(id: Int): UserDto {
      serviceTestingHelper.intercept()
      return providedUserDetails[id] ?: error("No user details provided for $id")
   }

   override fun getUsersStaleWhileRevalidate(
      limit: Int?,
      skip: Int?,
      force: Boolean,
   ): Flow<Outcome<UsersDto>> {
      lastReceivedLimitSkip = limit to skip
      val providedUsers = providedUsers ?: error("No users provided")
      val userList = providedUsers.drop(skip ?: 0).take(limit ?: Int.MAX_VALUE)

      return flowOf<Outcome<UsersDto>>(Outcome.Success(UsersDto(userList, providedUsers.size)))
         .onStart { serviceTestingHelper.intercept() }
   }

   override fun getUserStaleWhileRevalidate(
      id: Int,
      force: Boolean,
   ): Flow<Outcome<UserDto>> {
      return flowOf(Outcome.Success(providedUserDetails[id] ?: error("No user details provided for $id")))
         .onStart { serviceTestingHelper.intercept() }
   }
}
