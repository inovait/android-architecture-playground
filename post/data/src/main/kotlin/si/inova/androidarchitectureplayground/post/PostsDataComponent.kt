package si.inova.androidarchitectureplayground.post

import app.cash.sqldelight.db.SqlDriver
import me.tatarka.inject.annotations.Provides
import si.inova.androidarchitectureplayground.network.services.ServiceFactory
import si.inova.androidarchitectureplayground.network.services.create
import si.inova.androidarchitectureplayground.post.network.PostsService
import si.inova.androidarchitectureplayground.post.sqldelight.generated.Database
import si.inova.androidarchitectureplayground.post.sqldelight.generated.DbPostQueries
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface PostsDataComponent {
   @Provides
   @SingleIn(AppScope::class)
   fun providePostsService(serviceFactory: ServiceFactory): PostsService = serviceFactory.create()

   @Provides
   @SingleIn(AppScope::class)
   fun providePostQueries(driver: SqlDriver): DbPostQueries {
      return Database(driver).dbPostQueries
   }
}
