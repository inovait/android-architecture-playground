package si.inova.androidarchitectureplayground.navigation.conditions

import kotlinx.parcelize.Parcelize
import si.inova.kotlinova.navigation.conditions.NavigationCondition
import si.inova.kotlinova.navigation.di.NavigationContext
import si.inova.kotlinova.navigation.instructions.ClearBackstackAnd
import si.inova.kotlinova.navigation.instructions.NavigateWithConditions
import si.inova.kotlinova.navigation.instructions.NavigationInstruction
import si.inova.kotlinova.navigation.instructions.OpenScreenOrMoveToTop
import si.inova.kotlinova.navigation.instructions.ReplaceBackstack
import si.inova.kotlinova.navigation.screenkeys.ScreenKey

@Parcelize
object UserLoggedIn : NavigationCondition

interface NoLoginRedirectKey

/**
 * Navigation instruction that implements standard deep link behavior with forced login for all screens except login screen
 *
 * If [replaceBackstack] is *true*, it will replace the entire backstack with the provided [history]. Otherwise it will take
 * just the last screen of the provided [history] and put it on top of the backstack.
 *
 * This instruction also supports navigation conditions on the last screen. If screen has any unmet conditions, the condition
 * resolving screen will be displayed first, on an empty backstack. Then, action described in above paragraph will occur.
 *
 * This is a copy of the [ReplaceBackstackOrOpenScreenWithLogin].
 */
@Parcelize
class ReplaceBackstackOrOpenScreenWithLogin(val replaceBackstack: Boolean, vararg val history: ScreenKey) :
   NavigationInstruction() {
   override fun performNavigation(backstack: List<ScreenKey>, context: NavigationContext): NavigationResult {
      require(history.isNotEmpty()) { "You should provide at least one screen to ReplaceBackstackOrOpenScreen" }

      val finalScreen = history.last()
      val extraLoginCondition = if (finalScreen is NoLoginRedirectKey) {
         emptyList()
      } else {
         listOf(UserLoggedIn)
      }

      val navigationConditions = finalScreen.navigationConditions + extraLoginCondition
      val nextInstruction = if (replaceBackstack) {
         NavigateWithConditions(
            ReplaceBackstack(*history),
            *navigationConditions.toTypedArray(),
            conditionScreenWrapper = { ClearBackstackAnd(it) }
         )
      } else {
         NavigateWithConditions(
            OpenScreenOrMoveToTop(finalScreen),
            *navigationConditions.toTypedArray()
         )
      }

      return nextInstruction.performNavigation(backstack, context)
   }

   override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is ReplaceBackstackOrOpenScreenWithLogin) return false

      if (replaceBackstack != other.replaceBackstack) return false
      if (!history.contentEquals(other.history)) return false

      return true
   }

   override fun hashCode(): Int {
      var result = replaceBackstack.hashCode()
      result = 31 * result + history.contentHashCode()
      return result
   }

   override fun toString(): String {
      return "ReplaceBackstackOrOpenScreenWithLogin(replaceBackstack=$replaceBackstack, history=${history.contentToString()})"
   }
}
