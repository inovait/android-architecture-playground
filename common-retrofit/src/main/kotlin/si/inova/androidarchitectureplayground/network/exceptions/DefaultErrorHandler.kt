package si.inova.androidarchitectureplayground.network.exceptions

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import retrofit2.Response
import si.inova.kotlinova.core.outcome.CauseException
import si.inova.kotlinova.retrofit.callfactory.ErrorHandler

@Inject
class DefaultErrorHandler(
   private val json: Provider<Json>,
) : ErrorHandler {
   @OptIn(ExperimentalSerializationApi::class)
   override fun generateExceptionFromErrorBody(response: Response<*>, parentException: Exception): CauseException {
      @Suppress("MissingUseCall") // False positive. Calling this on the parent source is okay
      val errorMessage = response.errorBody()?.source()?.use {
         json().decodeFromStream<ErrorResponse>(ErrorResponse.serializer(), it.inputStream()).message
      } ?: "Unknown"

      return BackendException(errorMessage, parentException)
   }
}
