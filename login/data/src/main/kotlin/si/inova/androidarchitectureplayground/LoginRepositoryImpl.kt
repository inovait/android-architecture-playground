package si.inova.androidarchitectureplayground

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Qualifier
import si.inova.androidarchitectureplayground.login.LoginRepository
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class LoginRepositoryImpl @Inject constructor(
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
