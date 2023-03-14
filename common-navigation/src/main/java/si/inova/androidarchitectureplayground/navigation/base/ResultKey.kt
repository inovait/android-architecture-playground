package si.inova.androidarchitectureplayground.navigation.base

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ResultKey<T>(val key: Int) : Parcelable
