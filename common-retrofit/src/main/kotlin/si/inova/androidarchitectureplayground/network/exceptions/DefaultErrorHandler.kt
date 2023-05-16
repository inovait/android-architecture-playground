package si.inova.androidarchitectureplayground.network.exceptions

import com.squareup.moshi.Moshi
import retrofit2.Response
import si.inova.kotlinova.core.outcome.CauseException
import si.inova.kotlinova.retrofit.callfactory.ErrorHandler
import si.inova.kotlinova.retrofit.moshi.fromJson
import javax.inject.Inject
import javax.inject.Provider

class DefaultErrorHandler @Inject constructor(
   private val moshi: Provider<Moshi>
) : ErrorHandler {
   override fun generateExceptionFromErrorBody(response: Response<*>, parentException: Exception): CauseException? {
      val errorMessage = response.errorBody()?.source()?.let { moshi.get().fromJson<ErrorResponse>(it).message } ?: "Unknown"
      return BackendException(errorMessage, parentException)
   }
}
