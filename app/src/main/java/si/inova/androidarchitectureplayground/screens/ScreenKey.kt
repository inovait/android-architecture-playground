package si.inova.androidarchitectureplayground.screens

import android.os.Parcelable

abstract class ScreenKey : Parcelable {
   abstract val screenClass: String
}
