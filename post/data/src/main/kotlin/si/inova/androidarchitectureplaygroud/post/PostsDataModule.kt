package si.inova.androidarchitectureplaygroud.post

import app.cash.sqldelight.db.SqlDriver
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import si.inova.androidarchitectureplaygroud.post.network.PostsService
import si.inova.androidarchitectureplayground.common.di.PureApplicationScope
import si.inova.androidarchitectureplayground.network.services.ServiceFactory
import si.inova.androidarchitectureplayground.network.services.create
import si.inova.androidarchitectureplayground.post.sqldelight.generated.Database
import si.inova.androidarchitectureplayground.post.sqldelight.generated.DbPostQueries
import javax.inject.Singleton

@Module
@ContributesTo(PureApplicationScope::class)
object PostsDataModule {
   @Provides
   @Singleton
   fun providePostsService(serviceFactory: ServiceFactory): PostsService = serviceFactory.create()

   @Provides
   @Singleton
   fun providePostQueries(driver: SqlDriver): DbPostQueries {
      return Database(driver).dbPostQueries
   }
}
