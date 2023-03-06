package si.inova.androidarchitectureplayground.common.outcome

@Suppress("unused") // Constructors are mostly unused for now
abstract class CauseException : Exception {
   constructor() : super()
   constructor(message: String?) : super(message)
   constructor(message: String?, cause: Throwable?) : super(message, cause)
   constructor(cause: Throwable?) : super(cause)
   constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(
      message,
      cause,
      enableSuppression,
      writableStackTrace
   )

   abstract val isUsersFault: Boolean
   open val shouldReport: Boolean
      get() = !isUsersFault
}
