package com.androidarchitectureplayground.network.services

import retrofit2.Response

fun interface ErrorHandler {
   fun generateExceptionFromErrorBody(response: Response<*>, parentException: Exception): Exception?
}
