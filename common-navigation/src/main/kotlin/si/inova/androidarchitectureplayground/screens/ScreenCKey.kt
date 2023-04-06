package si.inova.androidarchitectureplayground.screens

import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.conditions.UserLoggedIn
import si.inova.kotlinova.navigation.conditions.NavigationCondition
import si.inova.kotlinova.navigation.screenkeys.SingleTopKey

@Parcelize
data class ScreenCKey(val number: Int, val key: String) : SingleTopKey() {
   override val navigationConditions: List<NavigationCondition>
      get() = listOf(UserLoggedIn)
}
