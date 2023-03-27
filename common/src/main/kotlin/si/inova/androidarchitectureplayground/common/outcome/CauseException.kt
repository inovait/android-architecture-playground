package si.inova.androidarchitectureplayground.common.outcome

@Suppress("MemberVisibilityCanBePrivate") // We want those to be accessible
abstract class CauseException(
   message: String? = null,
   cause: Throwable? = null,
   val isProgrammersFault: Boolean = true,
   val shouldReport: Boolean = isProgrammersFault
) : Exception(message, cause)
