package si.inova.androidarchitectureplayground.navigation.instructions

import android.os.Parcelable
import com.zhuinden.simplestack.Backstack

abstract class NavigationInstruction : Parcelable {
   abstract fun performNavigation(backstack: Backstack)
}
