package si.inova.androidarchitectureplayground.common.exceptions

import si.inova.androidarchitectureplayground.common.outcome.CauseException

// Even the fact that we got an unknown error, is a programmers fault.
// We should report all unknown errors by default and then make sure they are either fixed or
// mapped to a more useful cause
class UnknownCauseException(cause: Throwable? = null, message: String? = null) : CauseException(cause = cause, message = message)
