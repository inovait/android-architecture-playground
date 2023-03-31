package si.inova.androidarchitectureplayground.purekotlin

import com.squareup.anvil.annotations.ContributesBinding
import si.inova.kotlinova.core.di.PureApplicationScope
import si.inova.kotlinova.core.logging.logcat
import javax.inject.Inject

@ContributesBinding(PureApplicationScope::class)
class ClassFromPureKotlinModuleImpl @Inject constructor() : ClassFromPureKotlinModule {
   @Suppress("ALL")
   override fun getNumber(): Int {
      logcat { "Log from pure kotlin module" }
      return 6
   }
}
