package si.inova.androidarchitectureplayground.navigation.keys

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import si.inova.kotlinova.navigation.screenkeys.SingleTopKey

@Parcelize
data class HomeScreenKey(
   val selectedTab: Tab = Tab.POSTS,
   val userDetailsId: String? = null,
) : SingleTopKey() {
   @Parcelize
   sealed class Tab : Parcelable {
      @Parcelize
      data object POSTS : Tab()

      @Parcelize
      data object USERS : Tab()

      @Parcelize
      data object SETTINGS : Tab()
   }
}
