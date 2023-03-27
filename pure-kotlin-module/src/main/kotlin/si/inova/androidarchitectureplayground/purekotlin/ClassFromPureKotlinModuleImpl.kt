package si.inova.androidarchitectureplayground.purekotlin

import com.squareup.anvil.annotations.ContributesBinding
import si.inova.androidarchitectureplayground.common.PureApplicationScope
import si.inova.androidarchitectureplayground.common.logging.logcat
import javax.inject.Inject

@ContributesBinding(PureApplicationScope::class)
class ClassFromPureKotlinModuleImpl @Inject constructor() : ClassFromPureKotlinModule {
   @Suppress("ALL")
   override fun getNumber(): Int {
      logcat { "Log from pure kotlin module" }
      return 6
   }
}
