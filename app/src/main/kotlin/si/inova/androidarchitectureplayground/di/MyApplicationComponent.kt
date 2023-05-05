package si.inova.androidarchitectureplayground.di

import android.app.Application
import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.app.ApplicationComponent
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import si.inova.androidarchitectureplayground.common.di.PureApplicationScope
import si.inova.kotlinova.navigation.di.OuterNavigationScope
import javax.inject.Singleton

@MergeComponent(ApplicationScope::class)
@MergeComponent(PureApplicationScope::class)
@MergeComponent(OuterNavigationScope::class)
@SingleIn(ApplicationScope::class)
@Singleton
interface MyApplicationComponent : ApplicationComponent {
   @Component.Factory
   interface Factory {
      fun create(
         @BindsInstance
         application: Application
      ): MyApplicationComponent
   }
}
