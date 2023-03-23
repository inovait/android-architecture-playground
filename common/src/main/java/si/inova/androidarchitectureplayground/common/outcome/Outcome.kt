package si.inova.androidarchitectureplayground.common.outcome

sealed class Outcome<out T> {
   abstract val data: T?

   data class Progress<out T>(override val data: T? = null, val progress: Float? = null) : Outcome<T>()
   data class Success<out T>(override val data: T) : Outcome<T>()
   data class Error<out T>(val exception: CauseException, override val data: T? = null) : Outcome<T>()
}
