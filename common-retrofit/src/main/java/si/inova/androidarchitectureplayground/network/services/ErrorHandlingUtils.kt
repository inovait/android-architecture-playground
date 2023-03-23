package si.inova.androidarchitectureplayground.network.services

import retrofit2.Response
import si.inova.androidarchitectureplayground.common.outcome.CauseException

/**
 * Returns either a response, if this response was successful or throws a [CauseException] that matches error body
 * (parsed via provided [ErrorHandler]).
 */
fun <T> Response<T>.bodyOrThrow(errorHandler: ErrorHandler? = null): T = if (isSuccessful) {
   val body = body()
   @Suppress("IfThenToElvis") // This looks better
   if (body == null) {
      // Body can be null when server returns 204. Assume we are expecting
      // Unit type and return it accordingly
      @Suppress("UNCHECKED_CAST")
      Unit as T
   } else {
      body
   }
} else {
   val exception = try {
      val rawRequestException = createParentException()

      errorHandler?.generateExceptionFromErrorBody(this, rawRequestException)
         ?: rawRequestException.transformRetrofitException()
   } catch (e: Exception) {
      e.transformRetrofitException()
   }

   throw exception
}

internal fun Response<*>.createParentException(): Exception {
   return Exception("Endpoint call to ${raw().request.url} failed: ${code()} ${message()}")
}
