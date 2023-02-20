package si.inova.androidarchitectureplayground.simplestack

import com.zhuinden.simplestack.StateChange
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

data class StateChangeResult(
   /**
    * Either [StateChange.REPLACE], [StateChange.FORWARD], [StateChange.BACKWARD]
    */
   val direction: Int,
   val newTopKey: ScreenKey
)
