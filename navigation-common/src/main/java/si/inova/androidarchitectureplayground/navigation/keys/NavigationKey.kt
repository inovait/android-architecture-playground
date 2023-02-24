package si.inova.androidarchitectureplayground.navigation.keys

import android.os.Parcelable
import com.zhuinden.simplestack.Backstack

abstract class NavigationKey : Parcelable {
   abstract fun performNavigation(backstack: Backstack)
}
