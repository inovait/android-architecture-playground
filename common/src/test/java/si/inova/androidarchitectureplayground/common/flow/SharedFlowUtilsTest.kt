package si.inova.androidarchitectureplayground.common.flow

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class SharedFlowUtilsTest {
   @Test
   internal fun `Update hasActiveSubscribersFlow when number of collectors change`() = runTest {
      val flow = MutableStateFlow(Unit)

      flow.hasActiveSubscribersFlow().test {
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
   internal fun `Do not re-emit if state has not changed`() = runTest {
      val flow = MutableStateFlow(Unit)

      flow.hasActiveSubscribersFlow().test {
         awaitItem() shouldBe false

         val collectorJobA = flow.launchIn(this@runTest)
         runCurrent()
         awaitItem()

         val collectorJobB = flow.launchIn(this@runTest)
         runCurrent()
         collectorJobA.cancel()
         runCurrent()

         expectNoEvents()
         collectorJobB.cancel()
         cancelAndIgnoreRemainingEvents()
      }
   }
}
