package si.inova.androidarchitectureplayground.common.logging

/**
 * An enum for log priorities
 *
 * From https://github.com/square/logcat/blob/f877f806e8dce98a3abbc2ca80e6447b38cd8f42/logcat/src/main/java/logcat/LogPriority.kt
 */
enum class LogPriority(
   val priorityInt: Int
) {
   VERBOSE(2),
   DEBUG(3),
   INFO(4),
   WARN(5),
   ERROR(6),
   ASSERT(7);
}
