package si.inova.androidarchitectureplayground.purekotlin

import javax.inject.Inject

class ClassFromPureKotlinModule @Inject constructor() {
   @Suppress("FunctionOnlyReturningConstant")
   fun getNumber(): Int {
      return 6
   }
}
