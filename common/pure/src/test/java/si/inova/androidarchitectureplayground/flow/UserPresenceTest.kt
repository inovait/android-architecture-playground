package si.inova.androidarchitectureplayground.flow

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test

internal class UserPresenceTest {
   @Test
   internal fun `Update UserPresenceProvider when number of collectors change`() = runTest {
      val flow = MutableStateFlow(Unit)
      val presenceProvider = UserPresenceProvider(flow)

      presenceProvider.isUserPresentFlow().test {
         awaitItem() shouldBe false

         val collectorJob = flow.launchIn(this@runTest)
         runCurrent()
         awaitItem() shouldBe true

         collectorJob.cancel()
         runCurrent()
         awaitItem() shouldBe false

         expectNoEvents()
      }
   }

   @Test
   internal fun `Only flow when user is present`() = runTest {
      val presenceProvider = FakeUserPresenceProvider()
      presenceProvider.isPresent = true

      withContext(presenceProvider) {
         val source = MutableSharedFlow<Int>()

         source.onlyFlowWhenUserPresent().test {
            source.subscriptionCount.value shouldBe 1
            source.emit(1)
            awaitItem() shouldBe 1

            presenceProvider.isPresent = false
            source.subscriptionCount.value shouldBe 0
            source.emit(2)
            expectNoEvents()

            presenceProvider.isPresent = true
            source.subscriptionCount.value shouldBe 1
            source.emit(3)
            awaitItem() shouldBe 3

            expectNoEvents()
         }
      }
   }

   @Test
   internal fun `Trigger optional start parameter when flow starts without user present`() = runTest {
      val presenceProvider = FakeUserPresenceProvider()
      presenceProvider.isPresent = false

      withContext(presenceProvider) {
         val source = MutableSharedFlow<Int>()

         source.onlyFlowWhenUserPresent { emit(4) }.test {
            runCurrent()

            source.subscriptionCount.value shouldBe 0
            awaitItem() shouldBe 4

            presenceProvider.isPresent = true
            source.subscriptionCount.value shouldBe 1
            source.emit(1)
            awaitItem() shouldBe 1

            presenceProvider.isPresent = false
            source.subscriptionCount.value shouldBe 0
            source.emit(2)
            expectNoEvents()

            presenceProvider.isPresent = true
            source.subscriptionCount.value shouldBe 1
            source.emit(3)
            awaitItem() shouldBe 3

            expectNoEvents()
         }
      }
   }
}
