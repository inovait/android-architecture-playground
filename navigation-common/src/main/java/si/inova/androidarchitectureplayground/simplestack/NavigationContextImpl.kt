package si.inova.androidarchitectureplayground.simplestack

import si.inova.androidarchitectureplayground.navigation.base.NavigationContext
import javax.inject.Inject

class NavigationContextImpl @Inject constructor(
   override val mainConditionalNavigationHandler: MainConditionalNavigationHandler
) : NavigationContext
