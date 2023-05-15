package si.inova.androidarchitectureplayground.user.exceptions

import si.inova.kotlinova.core.outcome.CauseException

class UnknownUserException(cause: Throwable) : CauseException(cause = cause)
