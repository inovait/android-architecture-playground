package si.inova.androidarchitectureplayground.di

import android.app.Application
import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dagger.Provides
import si.inova.androidarchitectureplayground.Database
import si.inova.androidarchitectureplayground.common.di.ApplicationScope
import si.inova.kotlinova.core.time.AndroidDateTimeFormatter
import si.inova.kotlinova.core.time.AndroidDateTimeFormatterImpl
import si.inova.kotlinova.core.time.AndroidTimeProvider
import si.inova.kotlinova.core.time.DefaultAndroidTimeProvider
import si.inova.kotlinova.core.time.TimeProvider
import javax.inject.Singleton

@Suppress("unused")
@ContributesTo(ApplicationScope::class)
@Module
abstract class AppModule {
   @Binds
   abstract fun bindToContext(application: Application): Context

   @Binds
   abstract fun bindToTimeProvider(androidTimeProvider: AndroidTimeProvider): TimeProvider

   @Binds
   abstract fun bindToAndroidDateTimeFormatter(
      androidDateTimeFormatterImpl: AndroidDateTimeFormatterImpl,
   ): AndroidDateTimeFormatter

   @Module
   companion object {
      @Provides
      fun provideAndroidTimeProvider(): AndroidTimeProvider {
         return DefaultAndroidTimeProvider
      }

      @Provides
      @Singleton
      fun provideSqliteDriver(context: Context): SqlDriver {
         return AndroidSqliteDriver(Database.Schema, context, "database.db")
      }
   }
}
