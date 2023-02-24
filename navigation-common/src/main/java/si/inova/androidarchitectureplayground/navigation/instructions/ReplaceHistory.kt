package si.inova.androidarchitectureplayground.navigation.instructions

import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.StateChange
import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.keys.InitialNavigationKey
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

@Parcelize
class ReplaceHistory(vararg val history: ScreenKey) : NavigationInstruction(), InitialNavigationKey {
   override fun performNavigation(backstack: Backstack) {
      backstack.setHistory(history.toList(), StateChange.REPLACE)
   }

   override fun getInitialHistory(): List<ScreenKey> {
      return history.toList()
   }
}
