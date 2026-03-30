package si.inova.androidarchitectureplayground.network.exceptions

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val message: String)
