package si.inova.androidarchitectureplayground.di

import android.app.Application
import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import me.tatarka.inject.annotations.Provides
import si.inova.androidarchitectureplayground.Database
import si.inova.kotlinova.core.reporting.ErrorReporter
import si.inova.kotlinova.core.time.AndroidDateTimeFormatter
import si.inova.kotlinova.core.time.AndroidDateTimeFormatterImpl
import si.inova.kotlinova.core.time.AndroidTimeProvider
import si.inova.kotlinova.core.time.DefaultAndroidTimeProvider
import si.inova.kotlinova.core.time.TimeProvider
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface CommonInjectionsComponent {
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
