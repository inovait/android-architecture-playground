package si.inova.libmodule

import android.content.Context
import android.content.res.Resources
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier

@Suppress("unused")
@ContributesTo(ApplicationScope::class)
@Module
class TestModule {
   @Provides
   fun provideResources(context: Context): Resources = context.resources

   @Provides
   @TestQualifier
   fun provideHello(): String = "hello"
}

@Qualifier
annotation class TestQualifier
