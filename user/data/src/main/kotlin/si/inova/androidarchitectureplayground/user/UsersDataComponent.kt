package si.inova.androidarchitectureplayground.user

import app.cash.sqldelight.db.SqlDriver
import me.tatarka.inject.annotations.Provides
import si.inova.androidarchitectureplayground.network.services.ServiceFactory
import si.inova.androidarchitectureplayground.network.services.create
import si.inova.androidarchitectureplayground.user.network.UsersService
import si.inova.androidarchitectureplayground.user.sqldelight.generated.Database
import si.inova.androidarchitectureplayground.user.sqldelight.generated.DbUserQueries
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface UsersDataComponent {
   @Provides
   @SingleIn(AppScope::class)
   fun provideUsersService(serviceFactory: ServiceFactory): UsersService = serviceFactory.create()

   @Provides
   @SingleIn(AppScope::class)
   fun provideUserQueries(driver: SqlDriver): DbUserQueries {
      return Database(driver).dbUserQueries
   }
}
