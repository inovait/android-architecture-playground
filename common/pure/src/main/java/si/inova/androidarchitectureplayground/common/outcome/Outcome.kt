package si.inova.androidarchitectureplayground.common.outcome

sealed class Outcome<out T> {
   data class Progress<out T>(val data: T? = null, val progress: Float? = null) : Outcome<T>()
   data class Success<out T>(val data: T) : Outcome<T>()
   data class Error<out T>(val exception: CauseException, val data: T? = null) : Outcome<T>()
}

val <T> Outcome<T>.valueOrNull: T?
   get() {
      return when (this) {
         is Outcome.Error -> data
         is Outcome.Progress -> data
         is Outcome.Success -> data
      }
   }
