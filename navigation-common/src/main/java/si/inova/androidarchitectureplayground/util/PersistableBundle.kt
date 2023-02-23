package si.inova.androidarchitectureplayground.util

import android.os.Parcelable
import com.zhuinden.statebundle.StateBundle
import java.io.Serializable

/**
 * Helper single method to set any object into bundle without using different set methods for different types
 */
operator fun StateBundle.set(key: String, value: Any) {
   when (value) {
      is String -> putString(key, value)
      is Int -> putInt(key, value)
      is Short -> putShort(key, value)
      is Long -> putLong(key, value)
      is Byte -> putByte(key, value)
      is ByteArray -> putByteArray(key, value)
      is Char -> putChar(key, value)
      is CharArray -> putCharArray(key, value)
      is CharSequence -> putCharSequence(key, value)
      is Float -> putFloat(key, value)
      is StateBundle -> putBundle(key, value)
      is Parcelable -> putParcelable(key, value)
      is Serializable -> putSerializable(key, value)
      else -> error(
         "Type ${value.javaClass.canonicalName} of property $key is not supported in bundle"
      )
   }
}
