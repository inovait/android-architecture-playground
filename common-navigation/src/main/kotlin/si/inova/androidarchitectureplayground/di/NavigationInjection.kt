package si.inova.androidarchitectureplayground.di

import com.zhuinden.simplestack.Backstack
import si.inova.androidarchitectureplayground.navigation.Navigator
import si.inova.androidarchitectureplayground.navigation.base.ScopedService
import si.inova.androidarchitectureplayground.simplestack.NavigationContextImpl
import si.inova.androidarchitectureplayground.simplestack.ScreenRegistry
import javax.inject.Provider

interface NavigationInjection {
   fun screenRegistry(): ScreenRegistry
   fun scopedServicesFactories(): Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Provider<ScopedService>>
   fun navigator(): Navigator
   fun navigationContext(): NavigationContextImpl

   interface Factory {
      fun create(
         backstack: Backstack,
         @MainNavigation
         mainBackstack: Backstack
      ): NavigationInjection
   }
}
