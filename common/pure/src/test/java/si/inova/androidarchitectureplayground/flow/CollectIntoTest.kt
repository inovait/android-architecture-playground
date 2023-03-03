package si.inova.androidarchitectureplayground.flow

import app.cash.turbine.test
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class CollectIntoTest {
   @Test
   internal fun collectInto() = runTest {
      val target = MutableSharedFlow<Int>()
      val source = flowOf(1, 2, 3)

      target.test {
         source.collectInto(target)

         awaitItem() shouldBe 1
         awaitItem() shouldBe 2
         awaitItem() shouldBe 3
         expectNoEvents()
      }
   }

   @Test
   internal fun launchAndCollectInto() = runTest {
      val target = MutableSharedFlow<Int>()
      val source = flowOf(1, 2, 3)

      target.test {
         source.launchAndCollectInto(this@runTest, target)
         runCurrent()

         awaitItem() shouldBe 1
         awaitItem() shouldBe 2
         awaitItem() shouldBe 3
         expectNoEvents()
      }
   }

   @Test
   internal fun provideUserPresenceWhenUsingCollectInto() = runTest {
      val target = MutableStateFlow(false)
      val source = flow {
         val presenceProvider = currentCoroutineContext()[UserPresenceProvider].shouldNotBeNull()
         emitAll(presenceProvider.isUserPresentFlow())
      }

      val collectIntoJob = source.launchAndCollectInto(this, target)

      target.value shouldBe false

      val collectorJob = target.launchIn(this@runTest)
      runCurrent()
      target.value shouldBe true

      collectorJob.cancel()
      runCurrent()
      target.value shouldBe false

      collectIntoJob.cancel()
   }
}
