package si.inova.androidarchitectureplayground.di

import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.MergeSubcomponent
import com.zhuinden.simplestack.Backstack
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Inject

@MergeSubcomponent(NavigationStackScope::class)
interface NavigationStackComponent : NavigationInjection {

   @Subcomponent.Factory
   interface Factory {
      fun create(
         @BindsInstance
         backstack: Backstack,
         @BindsInstance
         @MainNavigation
         mainBackstack: Backstack
      ): NavigationStackComponent
   }
}

@ContributesBinding(ApplicationScope::class)
class FactoryImpl @Inject constructor(private val parentFactory: NavigationStackComponent.Factory) : NavigationInjection.Factory {
   override fun create(backstack: Backstack, mainBackstack: Backstack): NavigationInjection {
      return parentFactory.create(backstack, mainBackstack)
   }
}
