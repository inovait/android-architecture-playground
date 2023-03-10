package com.androidarchitectureplayground.network.services

import retrofit2.Response
import si.inova.androidarchitectureplayground.common.outcome.CauseException

fun interface ErrorHandler {
   fun generateExceptionFromErrorBody(response: Response<*>, parentException: Exception): CauseException?
}
