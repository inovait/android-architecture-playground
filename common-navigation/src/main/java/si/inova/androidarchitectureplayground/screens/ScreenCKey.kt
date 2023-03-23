package si.inova.androidarchitectureplayground.screens

import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.base.NavigationCondition
import si.inova.androidarchitectureplayground.navigation.conditions.UserLoggedIn
import si.inova.androidarchitectureplayground.navigation.keys.SingleTopKey

@Parcelize
data class ScreenCKey(val number: Int, val key: String) : SingleTopKey() {
   override val screenClass: String
      get() = "si.inova.androidarchitectureplayground.screens.ScreenC"

   override val navigationConditions: List<NavigationCondition>
      get() = listOf(UserLoggedIn)
}
