package si.inova.androidarchitectureplayground.test.outcomes

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.errorCollector
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import si.inova.androidarchitectureplayground.common.outcome.Outcome

infix fun <T> Outcome<T>.shouldBeSuccessWithData(expectedData: T) {
   assertSoftly {
      this
         .shouldBeInstanceOf<Outcome.Success<T>>()
         .data
         .let {
            withClue("Outcome's data does not match") {
               it.shouldBe(expectedData)
            }
         }

      if (this is Outcome.Error) {
         errorCollector.collectOrThrow(exception)
      }
   }
}

infix fun <T> Outcome<T>.shouldBeProgressWithData(expectedData: T?) {
   assertSoftly {
      this
         .shouldBeInstanceOf<Outcome.Progress<T>>()
         .data
         .let {
            withClue("Outcome's data does not match") {
               it.shouldBe(expectedData)
            }
         }

      if (this is Outcome.Error) {
         errorCollector.collectOrThrow(exception)
      }
   }
}
