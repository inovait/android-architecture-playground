package si.inova.androidarchitectureplayground

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dispatch.core.IOCoroutineScope
import si.inova.androidarchitectureplayground.common.di.ApplicationScope
import javax.inject.Singleton

@Module
@ContributesTo(ApplicationScope::class)
class LoginModule {
   @Provides
   @Singleton
   @LoginDataStore
   fun provideLoginDataStore(context: Context, ioCoroutineScope: IOCoroutineScope): DataStore<Preferences> {
      return PreferenceDataStoreFactory.create(scope = ioCoroutineScope) {
         context.preferencesDataStoreFile("login")
      }
   }
}
