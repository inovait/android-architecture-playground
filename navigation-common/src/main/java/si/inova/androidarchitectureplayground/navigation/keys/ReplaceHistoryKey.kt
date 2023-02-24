package si.inova.androidarchitectureplayground.navigation.keys

import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.StateChange
import kotlinx.parcelize.Parcelize

@Parcelize
class ReplaceHistoryKey(vararg val history: ScreenKey) : NavigationKey(), InitialNavigationKey {
   override fun performNavigation(backstack: Backstack) {
      backstack.setHistory(history.toList(), StateChange.REPLACE)
   }

   override fun getInitialHistory(): List<ScreenKey> {
      return history.toList()
   }
}
