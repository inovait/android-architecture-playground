package si.inova.androidarchitectureplayground.common.exceptions

import si.inova.androidarchitectureplayground.common.outcome.CauseException

class NoNetworkException(message: String? = null, cause: Throwable? = null) :
   CauseException(message, cause, isProgrammersFault = false)
