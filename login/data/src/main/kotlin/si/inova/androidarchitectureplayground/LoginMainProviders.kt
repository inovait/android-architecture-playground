package si.inova.androidarchitectureplayground

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dispatch.core.IOCoroutineScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
interface LoginMainProviders {
   @Provides
   @SingleIn(AppScope::class)
   @LoginDataStore
   fun provideLoginDataStore(context: Context, ioCoroutineScope: IOCoroutineScope): DataStore<Preferences> {
      return PreferenceDataStoreFactory.create(scope = ioCoroutineScope) {
         context.preferencesDataStoreFile("login")
      }
   }
}
