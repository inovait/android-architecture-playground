package si.inova.androidarchitectureplayground.user.ui.list

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import app.cash.turbine.test
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.paging.pagedListOf
import si.inova.androidarchitectureplayground.user.FakeUserRepository
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.test.outcomes.shouldBeProgressWithData
import si.inova.kotlinova.core.test.outcomes.shouldBeSuccessWithData
import si.inova.kotlinova.core.test.outcomes.testCoroutineResourceManager

class UserListViewModelImplTest {
   private val scope = TestScope()
   private val repository = FakeUserRepository()
   private val resources = scope.testCoroutineResourceManager()

   private val viewModel = UserListViewModelImpl(resources, repository, {})

   @Test
   fun `Load users`() = scope.runTest {
      repository.setUserList(
         PagingData.from(
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
            ),
            LoadStates(LoadState.NotLoading(false), LoadState.NotLoading(false), LoadState.NotLoading(false)),
            LoadStates(LoadState.NotLoading(false), LoadState.NotLoading(false), LoadState.NotLoading(false))
         )
      )

      viewModel.onServiceRegistered()

      viewModel.userList.test {
         runCurrent()

         expectMostRecentItem() shouldBeSuccessWithData pagedListOf(
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
            ),
         )
      }
   }

   @Test
   fun `Show loading`() = scope.runTest {
      repository.setUserList(
         PagingData.from(
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
            ),
            LoadStates(LoadState.Loading, LoadState.Loading, LoadState.Loading),
            LoadStates(LoadState.Loading, LoadState.Loading, LoadState.Loading)
         )
      )

      viewModel.onServiceRegistered()

      viewModel.userList.test {
         runCurrent()

         expectMostRecentItem() shouldBeProgressWithData pagedListOf(
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
            ),
         )
      }
   }
}
