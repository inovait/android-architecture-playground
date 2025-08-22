package si.inova.androidarchitectureplayground.user

import app.cash.sqldelight.db.SqlDriver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import si.inova.androidarchitectureplayground.network.services.ServiceFactory
import si.inova.androidarchitectureplayground.network.services.create
import si.inova.androidarchitectureplayground.user.network.UsersService
import si.inova.androidarchitectureplayground.user.sqldelight.generated.Database
import si.inova.androidarchitectureplayground.user.sqldelight.generated.DbUserQueries

@ContributesTo(AppScope::class)
interface UsersDataProviders {
   @Provides
   @SingleIn(AppScope::class)
   private fun provideUsersService(serviceFactory: ServiceFactory): UsersService = serviceFactory.create()

   @Provides
   @SingleIn(AppScope::class)
   private fun provideUserQueries(driver: SqlDriver): DbUserQueries {
      return createUserQueries(driver)
   }

   companion object {
      fun createUserQueries(driver: SqlDriver): DbUserQueries {
         return Database(driver).dbUserQueries
      }
   }
}
