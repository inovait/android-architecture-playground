package si.inova.androidarchitectureplayground.navigation.instructions

import android.os.Parcelable
import com.zhuinden.simplestack.StateChange
import si.inova.androidarchitectureplayground.navigation.base.NavigationContext
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

abstract class NavigationInstruction : Parcelable {
   abstract fun performNavigation(backstack: List<ScreenKey>, context: NavigationContext): NavigationResult

   /**
    * @param newBackstack New back stack after this navigation call.
    * @param direction The direction of the [StateChange]: [StateChange.BACKWARD], [StateChange.FORWARD] or [StateChange.REPLACE].
    */
   data class NavigationResult(
      val newBackstack: List<ScreenKey>,
      val direction: Int
   )
}
