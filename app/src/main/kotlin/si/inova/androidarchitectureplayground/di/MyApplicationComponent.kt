package si.inova.androidarchitectureplayground.di

import android.app.Application
import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.app.ApplicationComponent
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import si.inova.kotlinova.core.di.PureApplicationScope
import javax.inject.Singleton

@MergeComponent(ApplicationScope::class)
@MergeComponent(PureApplicationScope::class)
@SingleIn(ApplicationScope::class)
@Singleton
interface MyApplicationComponent : ApplicationComponent {
   fun provideNavigationStackComponentFactory(): NavigationStackComponent.Factory

   @Component.Factory
   interface Factory {
      fun create(
         @BindsInstance
         application: Application
      ): MyApplicationComponent
   }
}
