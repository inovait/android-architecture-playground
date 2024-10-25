package si.inova.androidarchitectureplayground

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dispatch.core.IOCoroutineScope
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface LoginMainComponent {
   @Provides
   @SingleIn(AppScope::class)
   @LoginDataStore
   fun provideLoginDataStore(context: Context, ioCoroutineScope: IOCoroutineScope): DataStore<Preferences> {
      return PreferenceDataStoreFactory.create(scope = ioCoroutineScope) {
         context.preferencesDataStoreFile("login")
      }
   }
}
