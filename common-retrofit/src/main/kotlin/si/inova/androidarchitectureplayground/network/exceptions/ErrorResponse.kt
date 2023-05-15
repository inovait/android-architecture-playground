package si.inova.androidarchitectureplayground.network.exceptions

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(val message: String)
