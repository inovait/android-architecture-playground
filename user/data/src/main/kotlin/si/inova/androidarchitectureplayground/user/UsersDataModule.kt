package si.inova.androidarchitectureplayground.user

import app.cash.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import si.inova.androidarchitectureplayground.network.services.ServiceFactory
import si.inova.androidarchitectureplayground.network.services.create
import si.inova.androidarchitectureplayground.user.network.UsersService
import si.inova.androidarchitectureplayground.user.sqldelight.generated.Database
import si.inova.androidarchitectureplayground.user.sqldelight.generated.DbUserQueries
import javax.inject.Singleton

@Module
object UsersDataModule {
   @Provides
   @Singleton
   fun provideUsersService(serviceFactory: ServiceFactory): UsersService = serviceFactory.create()

   @Provides
   @Singleton
   fun provideUserQueries(driver: SqlDriver): DbUserQueries {
      return Database(driver).dbUserQueries
   }
}
