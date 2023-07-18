package si.inova.androidarchitectureplayground.location

import kotlinx.coroutines.flow.Flow
import si.inova.androidarchitectureplayground.location.model.Location

interface LocationRetriever {
   fun getLocation(): Flow<Location>
}
