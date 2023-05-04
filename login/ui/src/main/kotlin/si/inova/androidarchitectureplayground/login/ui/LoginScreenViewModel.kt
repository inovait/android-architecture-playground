package si.inova.androidarchitectureplayground.login.ui

import kotlinx.coroutines.flow.StateFlow
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.services.ScopedService

interface LoginScreenViewModel : ScopedService {
   val loginStatus: StateFlow<Outcome<Unit>>
   fun login()
}
