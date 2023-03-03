package si.inova.androidarchitectureplayground.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUserPresenceProvider : UserPresenceProvider {
   private val flow = MutableStateFlow(false)

   var isPresent: Boolean
      get() = flow.value
      set(value) {
         flow.value = value
      }

   override fun isUserPresentFlow(): Flow<Boolean> {
      return flow
   }
}
