package si.inova.androidarchitectureplayground.common.flow

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import si.inova.kotlinova.core.flow.UserPresenceProvider
import si.inova.kotlinova.core.test.time.virtualTimeProvider
import kotlin.time.Duration.Companion.minutes

class AwayDetectorFlowTest {
   private val isUserPresent = MutableStateFlow<Boolean>(false)
   private val scope = TestScope(UserPresenceProvider { isUserPresent })

   private val awayDetectorFlow = AwayDetectorFlow(timeProvider = scope.virtualTimeProvider())

   @Test
   fun `Emit false by default when user is not present`() = scope.runTest {
      isUserPresent.value = false

      awayDetectorFlow.test {
         runCurrent()
         expectMostRecentItem() shouldBe false

         cancelAndIgnoreRemainingEvents()
      }
   }

   @Test
   fun `Emit false by default when user is present`() = scope.runTest {
      isUserPresent.value = false

      awayDetectorFlow.test {
         runCurrent()
         expectMostRecentItem() shouldBe false

         cancelAndIgnoreRemainingEvents()
      }
   }

   @Test
   fun `Do not emit anything leaves but comes back in less than 15 minutes`() = scope.runTest {
      isUserPresent.value = true

      awayDetectorFlow.test {
         runCurrent()
         expectMostRecentItem()

         isUserPresent.value = false
         delay(5.minutes)

         isUserPresent.value = true
         runCurrent()

         expectNoEvents()

         cancelAndIgnoreRemainingEvents()
      }
   }

   @Test
   fun `Emit true when user leaves but comes back in more than 15 minutes`() = scope.runTest {
      isUserPresent.value = true

      awayDetectorFlow.test {
         runCurrent()
         expectMostRecentItem()

         isUserPresent.value = false
         delay(16.minutes)

         isUserPresent.value = true
         runCurrent()

         expectMostRecentItem() shouldBe true

         cancelAndIgnoreRemainingEvents()
      }
   }

   @Test
   fun `Emit false when after emitting true, but recollecting`() = scope.runTest {
      isUserPresent.value = true

      awayDetectorFlow.test {
         runCurrent()
         expectMostRecentItem()

         isUserPresent.value = false
         delay(16.minutes)

         cancelAndIgnoreRemainingEvents()
      }

      awayDetectorFlow.test {
         runCurrent()
         expectMostRecentItem() shouldBe false
      }
   }

   @Test
   fun `Emit true when user leaves and comes back after first leave`() = scope.runTest {
      isUserPresent.value = true

      awayDetectorFlow.test {
         runCurrent()
         expectMostRecentItem()

         isUserPresent.value = false
         delay(16.minutes)

         isUserPresent.value = true
         runCurrent()

         expectMostRecentItem()

         isUserPresent.value = false
         delay(16.minutes)

         isUserPresent.value = true
         runCurrent()

         expectMostRecentItem() shouldBe true

         cancelAndIgnoreRemainingEvents()
      }
   }
}
