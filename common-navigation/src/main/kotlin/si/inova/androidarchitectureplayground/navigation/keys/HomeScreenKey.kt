package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.parcelize.Parcelize
import si.inova.kotlinova.navigation.screenkeys.SingleTopKey

@Parcelize
data class HomeScreenKey(val selectedTab: Tab = Tab.POSTS) : SingleTopKey() {
   enum class Tab {
      POSTS,
      USERS,
      SETTINGS;
   }
}
