package si.inova.androidarchitectureplayground.common.reporting

fun interface ErrorReporter {
   fun report(throwable: Throwable)
}
