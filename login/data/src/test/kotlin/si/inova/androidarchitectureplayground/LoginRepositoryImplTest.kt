package si.inova.androidarchitectureplayground

import androidx.datastore.preferences.core.emptyPreferences
import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.common.test.datastore.InMemoryDataStore

class LoginRepositoryImplTest {
   private val preferences = InMemoryDataStore(emptyPreferences())

   val repository = LoginRepositoryImpl(preferences)

   @Test
   fun `User should be logged out by default`() = runTest {
      repository.isLoggedInFlow().first() shouldBe false
   }

   @Test
   fun `Save login state`() = runTest {
      repository.isLoggedInFlow().test {
         awaitItem() shouldBe false

         repository.setLoggedIn(true)

         awaitItem() shouldBe true
      }
   }

   @Test
   fun `Remember login state persistently`() = runTest {
      repository.setLoggedIn(true)

      val newRepository = LoginRepositoryImpl(preferences)
      newRepository.isLoggedInFlow().first() shouldBe true
   }
}
