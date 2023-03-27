package si.inova.androidarchitectureplayground.common.flow

import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.common.exceptions.NoNetworkException
import si.inova.androidarchitectureplayground.common.outcome.Outcome
import si.inova.androidarchitectureplayground.test.outcomes.shouldBeErrorWith
import si.inova.androidarchitectureplayground.test.outcomes.shouldBeProgressWithData
import si.inova.androidarchitectureplayground.test.outcomes.shouldBeSuccessWithData
import si.inova.androidarchitectureplayground.test.util.testWithExceptions

internal class BlinkingPreventionTest {
   @Test
   internal fun `Do not switch to Loading for several milliseconds`() = runTest {
      val source = MutableStateFlow<Outcome<Int>>(Outcome.Success(1))
      val flowWithoutBlinking = source.withBlinkingPrevention()

      flowWithoutBlinking.testWithExceptions {
         awaitItem().shouldNotBeNull() shouldBeSuccessWithData 1

         source.value = Outcome.Progress(1)
         runCurrent()
         expectNoEvents()

         advanceTimeBy(150)
         runCurrent()
         awaitItem().shouldNotBeNull() shouldBeProgressWithData 1
      }
   }

   @Test
   internal fun `Switch to interim Loading immediately when doNotWaitForInterimLoadings is set`() = runTest {
      val source = MutableStateFlow<Outcome<Int>>(Outcome.Success(1))
      val flowWithoutBlinking = source.withBlinkingPrevention(
         doNotWaitForInterimLoadings = true
      )

      flowWithoutBlinking.testWithExceptions {
         awaitItem().shouldNotBeNull() shouldBeSuccessWithData 1

         source.value = Outcome.Progress(1)
         runCurrent()
         expectMostRecentItem().shouldNotBeNull() shouldBeProgressWithData 1
      }
   }

   @Test
   internal fun `Start with empty flow if flow starts with Loading for several seconds`() = runTest {
      val source = MutableStateFlow<Outcome<Int>>(Outcome.Progress(1))

      val flowWithoutBlinking = source.withBlinkingPrevention()

      flowWithoutBlinking.testWithExceptions {
         expectNoEvents()

         advanceTimeBy(150)
         runCurrent()
         awaitItem().shouldNotBeNull() shouldBeProgressWithData 1

         advanceTimeBy(9_000)
         expectNoEvents()
      }
   }

   @Test
   internal fun `Start with empty flow if flow starts with Loading for several seconds even if there is no data`() = runTest {
      val source = MutableStateFlow<Outcome<Int>>(Outcome.Progress())

      val flowWithoutBlinking = source.withBlinkingPrevention()

      flowWithoutBlinking.testWithExceptions {
         expectNoEvents()

         advanceTimeBy(150)
         runCurrent()
         awaitItem().shouldNotBeNull() shouldBeProgressWithData null

         advanceTimeBy(9_000)
         expectNoEvents()
      }
   }

   @Test
   internal fun `Start with empty flow if flow starts with Loading for several seconds even if interim wait is enabled`() =
      runTest {
         val source = MutableStateFlow<Outcome<Int>>(Outcome.Progress(1))

         val flowWithoutBlinking = source.withBlinkingPrevention(doNotWaitForInterimLoadings = true)

         flowWithoutBlinking.testWithExceptions {
            expectNoEvents()

            advanceTimeBy(150)
            runCurrent()
            awaitItem().shouldNotBeNull() shouldBeProgressWithData 1
         }
      }

   @Test
   internal fun `Switch directly to Success if initial Loading is very short`() = runTest {
      val source = MutableStateFlow<Outcome<Int>>(Outcome.Progress())

      val flowWithoutBlinking = source.withBlinkingPrevention()

      flowWithoutBlinking.testWithExceptions {
         expectNoEvents()

         advanceTimeBy(50)
         runCurrent()
         expectNoEvents()

         source.value = Outcome.Success(1)
         runCurrent()
         awaitItem().shouldNotBeNull() shouldBeSuccessWithData 1
      }
   }

   @Test
   internal fun `Switch directly to Error if initial Loading is very short`() = runTest {
      val source = MutableStateFlow<Outcome<Int>>(Outcome.Progress())

      val flowWithoutBlinking = source.withBlinkingPrevention()

      flowWithoutBlinking.testWithExceptions {
         expectNoEvents()

         advanceTimeBy(50)
         runCurrent()
         expectNoEvents()

         source.value = Outcome.Error(NoNetworkException(), data = 3)
         runCurrent()
         awaitItem().shouldNotBeNull().shouldBeErrorWith(expectedData = 3, exceptionType = NoNetworkException::class.java)
      }
   }

   @Test
   internal fun `Switch directly to next Success if interim Loading is very short`() = runTest {
      val source = MutableStateFlow<Outcome<Int>>(Outcome.Success(1))

      val flowWithoutBlinking = source.withBlinkingPrevention()

      flowWithoutBlinking.testWithExceptions {
         awaitItem().shouldNotBeNull() shouldBeSuccessWithData 1

         source.value = Outcome.Progress(2)
         advanceTimeBy(50)
         runCurrent()
         expectNoEvents()

         source.value = Outcome.Success(3)
         runCurrent()
         awaitItem().shouldNotBeNull() shouldBeSuccessWithData 3
      }
   }

   @Test
   internal fun `Keep loading active for a minimum amount of time before switching to Success`() = runTest {
      val source = MutableStateFlow<Outcome<Int>>(Outcome.Success(1))

      val flowWithoutBlinking = source.withBlinkingPrevention()

      flowWithoutBlinking.testWithExceptions {
         awaitItem() // Ignore initial Success

         source.value = Outcome.Progress(1)
         advanceTimeBy(150)
         runCurrent()
         awaitItem()

         advanceTimeBy(150)
         source.value = Outcome.Success(2)
         runCurrent()
         awaitItem().shouldNotBeNull() shouldBeProgressWithData 2

         advanceTimeBy(400)
         runCurrent()
         awaitItem().shouldNotBeNull() shouldBeSuccessWithData 2
      }
   }

   @Test
   internal fun `Keep loading active for a minimum amount of time before switching to Failure`() = runTest {
      val source = MutableStateFlow<Outcome<Int>>(Outcome.Success(1))

      val flowWithoutBlinking = source.withBlinkingPrevention()

      flowWithoutBlinking.testWithExceptions {
         awaitItem() // Ignore initial Success

         source.value = Outcome.Progress(1)
         advanceTimeBy(150)
         runCurrent()
         awaitItem()

         advanceTimeBy(150)
         source.value = Outcome.Error(NoNetworkException(), 2)
         runCurrent()
         awaitItem().shouldNotBeNull() shouldBeProgressWithData 2

         advanceTimeBy(400)
         runCurrent()
         awaitItem().shouldNotBeNull().shouldBeErrorWith(expectedData = 2, exceptionType = NoNetworkException::class.java)
      }
   }

   @Test
   internal fun `Switch to Success immediately if loading has been happening for some time`() = runTest {
      val source = MutableStateFlow<Outcome<Int>>(Outcome.Success(1))

      val flowWithoutBlinking = source.withBlinkingPrevention()

      flowWithoutBlinking.testWithExceptions {
         awaitItem() // Ignore initial Success

         source.value = Outcome.Progress(1)
         advanceTimeBy(150)
         runCurrent()
         awaitItem()

         advanceTimeBy(1000)
         source.value = Outcome.Success(2)
         runCurrent()
         awaitItem().shouldNotBeNull() shouldBeSuccessWithData 2
      }
   }

   @Test
   internal fun `Switch to Error immediately if loading has been happening for some time`() = runTest {
      val source = MutableStateFlow<Outcome<Int>>(Outcome.Success(1))

      val flowWithoutBlinking = source.withBlinkingPrevention()

      flowWithoutBlinking.testWithExceptions {
         awaitItem() // Ignore initial Success

         source.value = Outcome.Progress(1)
         advanceTimeBy(150)
         runCurrent()
         awaitItem()

         advanceTimeBy(1000)
         source.value = Outcome.Error(NoNetworkException())
         runCurrent()
         awaitItem().shouldNotBeNull().shouldBeErrorWith(exceptionType = NoNetworkException::class.java)
      }
   }
}
