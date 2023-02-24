package si.inova.androidarchitectureplayground.navigation.keys

/**
 * Key that will replace initial activity's history if it is navigated to as initial deep link
 */
interface InitialNavigationKey {
   fun getInitialHistory(): List<ScreenKey>
}
