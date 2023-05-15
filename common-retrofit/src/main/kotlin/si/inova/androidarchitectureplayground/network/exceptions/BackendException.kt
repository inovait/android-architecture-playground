package si.inova.androidarchitectureplayground.network.exceptions

import si.inova.kotlinova.core.outcome.CauseException

class BackendException(val backendMessage: String, cause: Throwable) : CauseException(backendMessage, cause)
