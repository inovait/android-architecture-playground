package si.inova.androidarchitectureplayground.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import si.inova.androidarchitectureplayground.simplestack.Navigator
import si.inova.androidarchitectureplayground.simplestack.SimpleStackNavigator

@Suppress("unused")
@ContributesTo(SimpleStackActivityScope::class)
@Module
abstract class NavigationModule {
   @Binds
   abstract fun SimpleStackNavigator.bindToNavigator(): Navigator
}
