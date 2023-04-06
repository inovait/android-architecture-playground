package si.inova.androidarchitectureplayground.screens

import android.util.Log
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesBinding
import dispatch.core.MainImmediateCoroutineScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import si.inova.kotlinova.navigation.services.SingleScreenViewModel
import javax.inject.Inject
import kotlin.random.Random

@ContributesBinding(ApplicationScope::class, boundType = ScreenCViewModel::class)
class ScreenCViewModelImpl @Inject constructor(
   scope: MainImmediateCoroutineScope,
) : SingleScreenViewModel<ScreenCKey>(scope), ScreenCViewModel {
   override var number by saved(0)

   @OptIn(ExperimentalStdlibApi::class)
   override fun onServiceRegistered() {
      Log.d("ViewModel", "got key $key")

      if (number == 0) {
         number = Random.nextInt()
      }

      coroutineScope.launch {
         Log.d("ScreenCViewModel", "Running in ${coroutineContext[CoroutineDispatcher]}")
         try {
            awaitCancellation()
         } finally {
            Log.d("ScreenCViewModel", "cancelled")
         }
      }
   }
}
