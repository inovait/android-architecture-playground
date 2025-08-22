package si.inova.androidarchitectureplayground.post

import app.cash.sqldelight.db.SqlDriver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import si.inova.androidarchitectureplayground.network.services.ServiceFactory
import si.inova.androidarchitectureplayground.network.services.create
import si.inova.androidarchitectureplayground.post.network.PostsService
import si.inova.androidarchitectureplayground.post.sqldelight.generated.Database
import si.inova.androidarchitectureplayground.post.sqldelight.generated.DbPostQueries

@ContributesTo(AppScope::class)
interface PostsDataProviders {
   @Provides
   @SingleIn(AppScope::class)
   private fun providePostsService(serviceFactory: ServiceFactory): PostsService = serviceFactory.create()

   @Provides
   @SingleIn(AppScope::class)
   private fun providePostQueries(driver: SqlDriver): DbPostQueries {
      return createPostQueries(driver)
   }

   companion object {
      fun createPostQueries(driver: SqlDriver): DbPostQueries {
         return Database(driver).dbPostQueries
      }
   }
}
