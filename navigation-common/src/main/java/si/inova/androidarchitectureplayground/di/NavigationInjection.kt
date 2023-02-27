package si.inova.androidarchitectureplayground.di

import com.zhuinden.simplestack.Backstack
import si.inova.androidarchitectureplayground.navigation.Navigator
import si.inova.androidarchitectureplayground.navigation.base.ScopedService
import si.inova.androidarchitectureplayground.navigation.base.Screen
import si.inova.androidarchitectureplayground.simplestack.NavigationContextImpl
import javax.inject.Provider

interface NavigationInjection {
   fun screenFactories(): Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Provider<Screen<*>>>
   fun scopedServicesFactories(): Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Provider<ScopedService>>
   fun scopedServicesKeys(): Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Provider<List<Class<*>>>>
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
