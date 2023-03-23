package si.inova.androidarchitectureplayground.test.outcomes

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.errorCollector
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import si.inova.androidarchitectureplayground.common.outcome.CauseException
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

fun <T> Outcome<T>.shouldBeProgressWith(
   expectedData: T? = data,
   expectedProgress: Float? = null
) {
   assertSoftly {
      this
         .shouldBeInstanceOf<Outcome.Progress<T>>()
         .apply {
            data
               .let {
                  withClue("Outcome's data does not match") {
                     it.shouldBe(expectedData)
                  }
               }
         }
         .apply {
            if (expectedProgress != null) {
               progress
                  .let {
                     withClue("Outcome's progress does not match") {
                        it.shouldBe(expectedProgress)
                     }
                  }
            }
         }

      if (this is Outcome.Error) {
         errorCollector.collectOrThrow(exception)
      }
   }
}

fun <T> Outcome<T>.shouldBeErrorWith(
   expectedData: T? = data,
   exceptionType: Class<out CauseException>? = null,
   exceptionMessage: String? = null
) {
   assertSoftly {
      this
         .shouldBeInstanceOf<Outcome.Error<T>>()
         .apply {
            data
               .let {
                  withClue("Outcome's data does not match") {
                     it.shouldBe(expectedData)
                  }
               }
         }
         .exception
         .apply {
            if (exceptionMessage != null) {
               message
                  .let {
                     withClue("Exception's message") {
                        it.shouldBe(exceptionMessage)
                     }
                  }
            }

            if (exceptionType != null) {
               javaClass
                  .let {
                     withClue("Exception's type does not match") {
                        it.shouldBe(exceptionType)
                     }
                  }
            }
         }
   }
}
