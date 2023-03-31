package si.inova.androidarchitectureplayground.network.services

import retrofit2.Response
import si.inova.kotlinova.core.outcome.CauseException

fun interface ErrorHandler {
   fun generateExceptionFromErrorBody(response: Response<*>, parentException: Exception): CauseException?
}
