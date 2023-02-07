package si.inova.androidarchitectureplayground.screens

import android.os.Parcelable
import com.zhuinden.simplestack.ScopeKey

abstract class ScreenKey : Parcelable, ScopeKey {
   abstract val screenClass: String

   override fun getScopeTag(): String {
      return toString()
   }
}
