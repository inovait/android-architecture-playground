package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.parcelize.Parcelize
import si.inova.kotlinova.navigation.screenkeys.SingleTopKey

@Parcelize
data class HomeScreenKey internal constructor(
   val selectedTabName: String,
   val userDetailsId: String?
) : SingleTopKey() {
   constructor(selectedTab: Tab = Tab.POSTS, userDetailsId: String? = null) : this(selectedTab.name, userDetailsId)

   val selectedTab: Tab
      get() = Tab.valueOf(selectedTabName)

   enum class Tab {
      POSTS,
      USERS,
      SETTINGS
   }
}
