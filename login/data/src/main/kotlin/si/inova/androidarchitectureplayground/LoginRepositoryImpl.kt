package si.inova.androidarchitectureplayground

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Qualifier
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import si.inova.androidarchitectureplayground.login.LoginRepository

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class LoginRepositoryImpl(
   @LoginDataStore
   private val preferences: DataStore<Preferences>,
) : LoginRepository {
   /**
    * We need synchronous access for the conditional navigation
    */
   private var memoryCachedValue: Boolean = false

   override val isLoggedIn: Boolean
      get() = memoryCachedValue

   override fun isLoggedInFlow(): Flow<Boolean> {
      return preferences.data
         .map { it.get(loggedInPreference) == true }
         .onEach { memoryCachedValue = it }
   }

   override suspend fun setLoggedIn(loggedIn: Boolean) {
      memoryCachedValue = loggedIn
      preferences.edit { it[loggedInPreference] = loggedIn }
   }
}

private val loggedInPreference = booleanPreferencesKey("loggedIn")

@Qualifier
annotation class LoginDataStore
