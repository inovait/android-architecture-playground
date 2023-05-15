package si.inova.androidarchitectureplayground.user.network

import si.inova.androidarchitectureplayground.user.network.model.LightUserDto
import si.inova.androidarchitectureplayground.user.network.model.UserDto
import si.inova.androidarchitectureplayground.user.network.model.UsersDto
import si.inova.kotlinova.retrofit.FakeService
import si.inova.kotlinova.retrofit.ServiceTestingHelper

class FakeUsersService(private val serviceTestingHelper: ServiceTestingHelper = ServiceTestingHelper()) : UsersService,
   FakeService by serviceTestingHelper {
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
}
