package si.inova.androidarchitectureplayground.screens

import androidx.compose.runtime.Stable
import si.inova.androidarchitectureplayground.navigation.base.ScopedService
import si.inova.androidarchitectureplayground.purekotlin.ClassFromPureKotlinModule
import javax.inject.Inject

@Stable
class SharedViewModel @Inject constructor(
   private val classFromPureKotlinModule: ClassFromPureKotlinModule
) : ScopedService {
   val number: Int
      get() = classFromPureKotlinModule.getNumber()
}
