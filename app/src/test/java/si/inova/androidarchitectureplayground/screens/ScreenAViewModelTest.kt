package si.inova.androidarchitectureplayground.screens

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.test.testMainImmediateBackgroundScope
import si.inova.androidarchitectureplayground.test.time.virtualTimeProvider

internal class ScreenAViewModelTest {
   @Test
   internal fun `Do A Task testing`() = runTest {
      val viewModel = ScreenAViewModel(testMainImmediateBackgroundScope, virtualTimeProvider())

      viewModel.doATask()
      advanceTimeBy(2_000)
      runCurrent()

      viewModel.result shouldBe 2
      viewModel.currentTimeMillis shouldBe 1_000
   }
}
