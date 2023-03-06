package si.inova.androidarchitectureplayground.common.exceptions

import si.inova.androidarchitectureplayground.common.outcome.CauseException

class NoNetworkException(message: String? = null, cause: Exception? = null) : CauseException(message, cause) {
   override val isUsersFault: Boolean
      get() = true
}