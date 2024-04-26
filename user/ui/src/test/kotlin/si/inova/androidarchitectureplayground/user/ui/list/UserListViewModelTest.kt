package si.inova.androidarchitectureplayground.user.ui.list

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.common.pagination.PaginatedDataStream
import si.inova.androidarchitectureplayground.user.FakeUserRepository
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.test.outcomes.shouldBeSuccessWithData
import si.inova.kotlinova.core.test.outcomes.testCoroutineResourceManager

class UserListViewModelTest {
   private val scope = TestScope()
   private val repository = FakeUserRepository()
   private val resources = scope.testCoroutineResourceManager()

   private val viewModel = UserListViewModel(resources, repository, {})

   @Test
   fun `Load users`() = scope.runTest {
      repository.setUserList(
         PaginatedDataStream.PaginationResult(
            items = Outcome.Success(
               listOf(
                  User(
                     id = 1,
                     firstName = "John",
                     lastName = "Doe",
                  ),
                  User(
                     id = 2,
                     firstName = "Jane",
                     lastName = "Smith",
                  )
               )
            ),
            hasAnyDataLeft = true
         )
      )

      viewModel.onServiceRegistered()

      viewModel.userList.test {
         runCurrent()

         expectMostRecentItem() shouldBeSuccessWithData UserListState(
            users = listOf(
               User(
                  id = 1,
                  firstName = "John",
                  lastName = "Doe",
               ),
               User(
                  id = 2,
                  firstName = "Jane",
                  lastName = "Smith",
               )
            ),
            hasAnyDataLeft = true
         )
      }
   }

   @Test
   fun `Load next page`() = scope.runTest {
      repository.setUserList(
         PaginatedDataStream.PaginationResult(
            items = Outcome.Success(
               listOf(
                  User(
                     id = 1,
                     firstName = "John",
                     lastName = "Doe",
                  ),
                  User(
                     id = 2,
                     firstName = "Jane",
                     lastName = "Smith",
                  )
               )
            ),
            hasAnyDataLeft = true
         )
      )

      viewModel.onServiceRegistered()

      viewModel.userList.test {
         runCurrent()

         viewModel.nextPage()
         runCurrent()

         repository.numTimesNextPageCalled shouldBe 1
         cancelAndConsumeRemainingEvents()
      }
   }
}
