package si.inova.androidarchitectureplayground.user.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import si.inova.androidarchitectureplayground.user.network.model.UserDto
import si.inova.androidarchitectureplayground.user.network.model.UsersDto

interface UsersService {
   @GET("users")
   suspend fun getUsers(
      @Query("limit")
      limit: Int? = null,
      @Query("skip")
      skip: Int? = null
   ): UsersDto

   @GET("users/{id}")
   suspend fun getUser(
      @Path("id")
      id: Int
   ): UserDto
}
