package si.inova.androidarchitectureplayground.common.exceptions

import si.inova.androidarchitectureplayground.common.outcome.CauseException

class JsonParsingException(message: String? = null, cause: Throwable? = null) : CauseException(message, cause)
