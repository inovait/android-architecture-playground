package si.inova.androidarchitectureplayground.purekotlin

import com.squareup.anvil.annotations.ContributesBinding
import si.inova.androidarchitectureplayground.PureApplicationScope
import javax.inject.Inject

@ContributesBinding(PureApplicationScope::class)
class ClassFromPureKotlinModuleImpl @Inject constructor() : ClassFromPureKotlinModule {
   @Suppress("FunctionOnlyReturningConstant")
   override fun getNumber(): Int {
      return 6
   }
}
