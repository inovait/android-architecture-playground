package si.inova.androidarchitectureplayground.post.exceptions

import si.inova.kotlinova.core.outcome.CauseException

class UnknownPostException(message: String? = null, cause: Throwable? = null) :
   CauseException(message, cause, isProgrammersFault = false)
