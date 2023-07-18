package si.inova.androidarchitectureplayground.location.model

/**
 * Replacement for Android's Location class that can be used in pure Kotlin modules
 */
data class Location(
   val latitude: Double,
   val longitude: Double
)
