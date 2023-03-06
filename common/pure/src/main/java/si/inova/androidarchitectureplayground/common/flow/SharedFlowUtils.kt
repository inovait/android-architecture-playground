package si.inova.androidarchitectureplayground.common.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

fun MutableSharedFlow<*>.hasActiveSubscribersFlow(): Flow<Boolean> {
   return subscriptionCount.map { it > 0 }.distinctUntilChanged()
}