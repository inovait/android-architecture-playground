package si.inova.androidarchitectureplayground

import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.Provides
import si.inova.androidarchitectureplayground.navigation.conditions.UserLoggedIn
import si.inova.kotlinova.navigation.conditions.ConditionalNavigationHandler
import si.inova.kotlinova.navigation.conditions.NavigationCondition
import si.inova.kotlinova.navigation.di.OuterNavigationScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

@ContributesTo(OuterNavigationScope::class)
interface LoginConditionComponent {
   @Provides
   @IntoMap
   fun provideLoginConditionalNavigationHandler(
      loginConditionalNavigationHandlerFactory: () -> LoginConditionalNavigationHandler,
   ): Pair<Class<out NavigationCondition>, () -> ConditionalNavigationHandler> =
      UserLoggedIn::class.java to loginConditionalNavigationHandlerFactory
}
