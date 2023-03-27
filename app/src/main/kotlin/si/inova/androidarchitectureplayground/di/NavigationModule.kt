package si.inova.androidarchitectureplayground.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import si.inova.androidarchitectureplayground.navigation.Navigator
import si.inova.androidarchitectureplayground.simplestack.SimpleStackNavigator

@Suppress("unused")
@ContributesTo(NavigationStackScope::class)
@Module
abstract class NavigationModule {
   @Binds
   abstract fun SimpleStackNavigator.bindToNavigator(): Navigator
}