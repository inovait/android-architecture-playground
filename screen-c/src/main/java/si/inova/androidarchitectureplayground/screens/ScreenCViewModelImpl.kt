package si.inova.androidarchitectureplayground.screens

import android.util.Log
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import si.inova.androidarchitectureplayground.di.NavigationStackScope
import si.inova.androidarchitectureplayground.navigation.base.SingleScreenViewModel
import javax.inject.Inject
import kotlin.random.Random

@ContributesBinding(NavigationStackScope::class, boundType = ScreenCViewModel::class)
class ScreenCViewModelImpl @Inject constructor() : SingleScreenViewModel<ScreenCKey>(), ScreenCViewModel {
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
