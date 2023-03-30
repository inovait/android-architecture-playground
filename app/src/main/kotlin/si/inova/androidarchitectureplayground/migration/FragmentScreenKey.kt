package si.inova.androidarchitectureplayground.migration

import android.os.Bundle
import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey
import java.util.UUID

@Parcelize
data class FragmentScreenKey(
   val fragmentClass: String,
   val arguments: Bundle? = null,
   val tag: String = UUID.randomUUID().toString()
) : ScreenKey()
