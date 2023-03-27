package si.inova.kotlinova.anvil_generator

import com.squareup.kotlinpoet.ClassName

internal val ACTIVITY_SCOPE_ANNOTATION = ClassName(
   "si.inova.androidarchitectureplayground.di",
   "NavigationStackScope"
)

internal val SIMPLE_STACK_SCOPED_ANNOTATION = ClassName(
   "si.inova.androidarchitectureplayground.navigation.base",
   "SimpleStackScoped"
)

internal val SCREEN_BASE_CLASS = ClassName(
   "si.inova.androidarchitectureplayground.navigation.base",
   "Screen"
)

internal val SCREEN_KEY_BASE_CLASS = ClassName(
   "si.inova.androidarchitectureplayground.navigation.keys",
   "ScreenKey"
)

internal val SCOPED_SERVICE_BASE_CLASS = ClassName(
   "si.inova.androidarchitectureplayground.navigation.base",
   "ScopedService"
)

internal val SIMPLE_STACK_BACKSTACK_CLASS = ClassName(
   "com.zhuinden.simplestack",
   "Backstack"
)

internal val APPLICATION_SCOPE_ANNOTATION = ClassName(
   "com.deliveryhero.whetstone.app",
   "ApplicationScope"
)