package si.inova.androidarchitectureplayground.user.network

import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import si.inova.androidarchitectureplayground.user.network.model.UserDto
import si.inova.androidarchitectureplayground.user.network.model.UsersDto
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.retrofit.SyntheticHeaders

interface UsersService {
   @GET("users")
   suspend fun getUsers(
      @Query("limit")
      limit: Int? = null,
      @Query("skip")
      skip: Int? = null,
   ): UsersDto

   @GET("users")
   fun getUsersStaleWhileRevalidate(
      @Query("limit")
      limit: Int? = null,
      @Query("skip")
      skip: Int? = null,
      @Header(SyntheticHeaders.HEADER_FORCE_REFRESH)
      force: Boolean = false,
   ): Flow<Outcome<UsersDto>>

   @GET("users/{id}")
   suspend fun getUser(
      @Path("id")
      id: Int,
   ): UserDto

   @GET("users/{id}")
   fun getUserStaleWhileRevalidate(
      @Path("id")
      id: Int,
      @Header(SyntheticHeaders.HEADER_FORCE_REFRESH)
      force: Boolean = false,
   ): Flow<Outcome<UserDto>>
}
