package com.androidarchitectureplayground.network.services

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import si.inova.androidarchitectureplayground.common.exceptions.NoNetworkException
import si.inova.androidarchitectureplayground.common.exceptions.UnknownCauseException
import si.inova.androidarchitectureplayground.common.outcome.CauseException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun transformRetrofitException(throwable: Throwable): CauseException {
   return when (throwable) {
      is SocketTimeoutException, is ConnectException, is UnknownHostException -> NoNetworkException(cause = throwable)
      is JsonDataException, is JsonEncodingException -> JsonParsingException(throwable)
      else -> UnknownCauseException(throwable)
   }
}

class JsonParsingException(cause: Throwable? = null) : CauseException(cause = cause)
