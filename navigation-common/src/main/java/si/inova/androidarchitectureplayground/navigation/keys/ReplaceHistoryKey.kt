package si.inova.androidarchitectureplayground.navigation.keys

import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.StateChange

class ReplaceHistoryKey(vararg val history: ScreenKey) : NavigationKey() {
   override fun performNavigation(backstack: Backstack) {
      backstack.setHistory(history.toList(), StateChange.REPLACE)
   }
}
