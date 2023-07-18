package si.inova.androidarchitectureplayground.location

import android.content.Context
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import si.inova.androidarchitectureplayground.common.di.ApplicationScope
import si.inova.androidarchitectureplayground.location.model.Location
import si.inova.kotlinova.core.logging.logcat
import javax.inject.Inject

@ContributesBinding(ApplicationScope::class)
@Suppress("MagicNumber")
class LocationRetrieverImpl @Inject constructor(
   private val context: Context
) : LocationRetriever {
   override fun getLocation(): Flow<Location> {
      val locationRequest = LocationRequest.Builder(10_000)
         .setMinUpdateDistanceMeters(100f)
         .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
         .build()

      val locationClient = LocationServices.getFusedLocationProviderClient(context)

      return callbackFlow {
         logcat { "Started getting location" }

         val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
               val androidLocation = result.lastLocation ?: return
               val ourLocation = Location(androidLocation.latitude, androidLocation.longitude)

               trySend(ourLocation)
            }
         }

         locationClient.requestLocationUpdates(locationRequest, callback, null)

         awaitClose {
            logcat { "Stopped getting location" }

            locationClient.removeLocationUpdates(callback)
         }
      }
   }
}
