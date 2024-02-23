package si.inova.androidarchitectureplayground.di

import android.app.Application
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import si.inova.androidarchitectureplayground.MainActivity
import si.inova.androidarchitectureplayground.common.di.ApplicationScope
import si.inova.kotlinova.navigation.di.OuterNavigationScope
import javax.inject.Singleton

@MergeComponent(ApplicationScope::class)
@MergeComponent(OuterNavigationScope::class)
@Singleton
interface MainApplicationComponent : ApplicationComponent {
   @Component.Factory
   interface Factory {
      fun create(
         @BindsInstance
         application: Application,
      ): MainApplicationComponent
   }
}

interface ApplicationComponent {
   fun inject(mainActivity: MainActivity)
}
