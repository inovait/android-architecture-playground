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

internal val SCREEN_REGISTRATION = ClassName(
   "si.inova.androidarchitectureplayground.simplestack",
   "ScreenRegistration"
)
