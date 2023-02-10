package si.inova.androidarchitectureplayground.screens

import android.os.Parcelable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import com.zhuinden.simplestack.ScopeKey
import com.zhuinden.simplestack.StateChange
import si.inova.androidarchitectureplayground.simplestack.StateChangeResult

abstract class ScreenKey : Parcelable, ScopeKey {
   abstract val screenClass: String

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
   open fun AnimatedContentScope<StateChangeResult>.forwardAnimation(): ContentTransform {
      return if (targetState.direction == StateChange.REPLACE) {
         fadeIn() with fadeOut()
      } else {
         slideIntoContainer(AnimatedContentScope.SlideDirection.Left) with
            slideOutOfContainer(AnimatedContentScope.SlideDirection.Left)
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
   open fun AnimatedContentScope<StateChangeResult>.backAnimation(): ContentTransform {
      return slideIntoContainer(AnimatedContentScope.SlideDirection.Right) with
         slideOutOfContainer(AnimatedContentScope.SlideDirection.Right)
   }
}
