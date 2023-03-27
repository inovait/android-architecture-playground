package si.inova.androidarchitectureplayground.navigation.keys

import android.os.Parcelable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import com.zhuinden.simplestack.ScopeKey
import com.zhuinden.simplestack.StateChange
import si.inova.androidarchitectureplayground.navigation.base.NavigationCondition
import si.inova.androidarchitectureplayground.simplestack.StateChangeResult

abstract class ScreenKey : Parcelable, ScopeKey {
   abstract val screenClass: String

   open val navigationConditions: List<NavigationCondition>
      get() = emptyList()

   override fun getScopeTag(): String {
      return toString()
   }

   /**
    * Animation that plays when this key is navigated to.
    *
    * This translates to [AnimatedContent]'s transitionSpec.
    * See [Compose documentation](https://developer.android.com/jetpack/compose/animation#animatedcontent)
    * for syntax and examples.
    */
   @OptIn(ExperimentalAnimationApi::class)
   open fun forwardAnimation(scope: AnimatedContentScope<StateChangeResult>): ContentTransform {
      return if (scope.targetState.direction == StateChange.REPLACE) {
         fadeIn() with fadeOut()
      } else {
         // Rough match of the FragmentTransaction.TRANSITION_OPEN

         val enter = scaleIn(
            tween(durationMillis = 150),
            initialScale = 0.90f
         ) + fadeIn(tween(durationMillis = 150))

         val exit = scaleOut(
            tween(durationMillis = 150),
            targetScale = 1.10f,
         ) + fadeOut(tween(delayMillis = 100, durationMillis = 50))

         enter with exit
      }
   }

   /**
    * Animation that plays when this key is popped of stack
    *
    * This translates to [AnimatedContent]'s transitionSpec.
    * See [Compose documentation](https://developer.android.com/jetpack/compose/animation#animatedcontent)
    * for syntax and examples.
    */
   @OptIn(ExperimentalAnimationApi::class)
   open fun backAnimation(scope: AnimatedContentScope<StateChangeResult>): ContentTransform {
      // Rough match of the FragmentTransaction.TRANSITION_CLOSE

      val enter = scaleIn(
         tween(durationMillis = 150),
         initialScale = 1.10f
      ) + fadeIn(tween(durationMillis = 150))

      val exit = scaleOut(
         tween(durationMillis = 150),
         targetScale = 0.90f,
      ) + fadeOut(tween(delayMillis = 100, durationMillis = 50))

      return enter with exit
   }

   // Force subclasses to implement toString, equals and hashCode, since compose and default getScopeTag relies on it.
   // This forces objects to implement it, but data classes are not affected since Kotlin generates toString for you.
   abstract override fun toString(): String
   abstract override fun equals(other: Any?): Boolean
   abstract override fun hashCode(): Int
}
