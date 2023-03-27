package si.inova.androidarchitectureplayground.navigation.keys

/**
 * Convenience middle-class for singleton keys without arguments.
 *
 * Any key extending this MUST be a singleton (such as a kotlin object).
 */
abstract class NoArgsScreenKey : ScreenKey() {
   override fun toString(): String {
      return javaClass.name
   }

   override fun equals(other: Any?): Boolean {
      return other === this
   }

   override fun hashCode(): Int {
      return toString().hashCode()
   }
}
