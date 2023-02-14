package si.inova.androidarchitectureplayground.screens

import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.StateChange

abstract class SingleTopKey : ScreenKey() {
   open val isSingleTop: Boolean = true

   override fun performNavigation(backstack: Backstack) {
      if (backstack.top<Any>().javaClass == this.javaClass) {
         backstack.replaceTop(this, StateChange.REPLACE)
      } else {
         super.performNavigation(backstack)
      }
   }
}
