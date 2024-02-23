package si.inova.androidarchitectureplayground.instrumentation

import android.app.Application
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import okhttp3.OkHttpClient
import si.inova.androidarchitectureplayground.common.di.ApplicationScope
import si.inova.androidarchitectureplayground.di.ApplicationComponent
import si.inova.kotlinova.navigation.di.OuterNavigationScope
import javax.inject.Singleton

@MergeComponent(ApplicationScope::class)
@MergeComponent(OuterNavigationScope::class)
@Singleton
interface TestAppComponent : ApplicationComponent {
   val okHttp: OkHttpClient

   @Component.Factory
   interface Factory {
      fun create(
         @BindsInstance
         application: Application,
      ): TestAppComponent
   }
}
