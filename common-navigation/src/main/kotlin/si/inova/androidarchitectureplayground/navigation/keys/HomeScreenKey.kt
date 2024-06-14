package si.inova.androidarchitectureplayground.navigation.keys

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.keys.base.BaseSingleTopScreenKey

@Parcelize
data class HomeScreenKey(
   val selectedTab: Tab = Tab.POSTS,
   val userDetailsId: String? = null,
) : BaseSingleTopScreenKey() {
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
