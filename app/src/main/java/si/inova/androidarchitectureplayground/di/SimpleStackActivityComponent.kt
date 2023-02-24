package si.inova.androidarchitectureplayground.di

import com.squareup.anvil.annotations.MergeSubcomponent
import com.zhuinden.simplestack.Backstack
import dagger.BindsInstance
import dagger.Subcomponent
import si.inova.androidarchitectureplayground.navigation.Navigator
import si.inova.androidarchitectureplayground.navigation.base.ScopedService
import si.inova.androidarchitectureplayground.navigation.base.Screen
import javax.inject.Provider

@MergeSubcomponent(SimpleStackActivityScope::class)
interface SimpleStackActivityComponent {
   fun screenFactories(): Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Provider<Screen<*>>>
   fun scopedServicesFactories(): Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Provider<ScopedService>>
   fun scopedServicesKeys(): Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Provider<List<Class<*>>>>
   fun navigator(): Navigator

   @Subcomponent.Factory
   interface Factory {
      fun create(
         @BindsInstance
         backstack: Backstack
      ): SimpleStackActivityComponent
   }
}
