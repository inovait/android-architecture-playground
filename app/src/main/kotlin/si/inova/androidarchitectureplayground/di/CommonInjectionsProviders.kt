package si.inova.androidarchitectureplayground.di

import android.app.Application
import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import si.inova.androidarchitectureplayground.Database
import si.inova.kotlinova.core.reporting.ErrorReporter
import si.inova.kotlinova.core.time.AndroidDateTimeFormatter
import si.inova.kotlinova.core.time.AndroidDateTimeFormatterImpl
import si.inova.kotlinova.core.time.AndroidTimeProvider
import si.inova.kotlinova.core.time.DefaultAndroidTimeProvider
import si.inova.kotlinova.core.time.TimeProvider

@ContributesTo(AppScope::class)
interface CommonInjectionsProviders {
   @Provides
   fun bindToContext(application: Application): Context = application

   @Provides
   fun bindToTimeProvider(androidTimeProvider: AndroidTimeProvider): TimeProvider = androidTimeProvider

   @Provides
   fun provideAndroidDateTimeFormatter(
      context: Context,
      errorReporter: ErrorReporter,
   ): AndroidDateTimeFormatter = AndroidDateTimeFormatterImpl(context, errorReporter)

   @Provides
   fun provideAndroidTimeProvider(): AndroidTimeProvider {
      return DefaultAndroidTimeProvider
   }

   @Provides
   @SingleIn(AppScope::class)
   fun provideSqliteDriver(context: Context): SqlDriver {
      return AndroidSqliteDriver(Database.Schema, context, "database.db")
   }
}
