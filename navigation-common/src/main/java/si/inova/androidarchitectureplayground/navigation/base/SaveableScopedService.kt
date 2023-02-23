package si.inova.androidarchitectureplayground.navigation.base

import com.zhuinden.simplestack.Bundleable
import com.zhuinden.statebundle.StateBundle
import si.inova.androidarchitectureplayground.util.set
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Scoped service that can save its state and gets recreated after process kill.
 * To use create properties using "by saved" delegation. For example:
 *
 * ```
 * class MyService: SaveableScopedService() {
 *   val savedNumber by saved(10)
 * }
 * ```
 */
abstract class SaveableScopedService : CoroutineScopedService(), Bundleable {
   protected var bundle = StateBundle()

   /**
    * Property that gets automatically saved and restored when this ScopedService gets recreated after process kill.
    *
    * @param defaultValue Default value that property has. This value NOT saved, so use a constant.
    * @param setNotification Lambda that gets triggered whenever this property is manually set.
    * This will NOT get triggered when value gets restored when ScopedService gets recreated
    */
   fun <T> saved(
      defaultValue: T,
      setNotification: ((T) -> Unit)? = null
   ): ReadWriteProperty<SaveableScopedService, T> {
      return StateSavedProperty(defaultValue, setNotification)
   }

   override fun toBundle(): StateBundle {
      return bundle
   }

   override fun fromBundle(bundle: StateBundle?) {
      if (bundle != null) {
         this.bundle = bundle
      }
   }

   private class StateSavedProperty<T>(
      defaultValue: T,
      private val setNotification: ((T) -> Unit)? = null
   ) : ReadWriteProperty<SaveableScopedService, T> {
      var value: T = defaultValue
      var initialized = false

      override fun getValue(thisRef: SaveableScopedService, property: KProperty<*>): T {
         if (!initialized) {
            @Suppress("UNCHECKED_CAST")
            (thisRef.bundle.get(property.name) as T)?.let { value = it }

            initialized = true
         }

         return value
      }

      override fun setValue(thisRef: SaveableScopedService, property: KProperty<*>, value: T) {
         initialized = true
         this.value = value

         if (value != null) {
            thisRef.bundle[property.name] = value
         } else {
            thisRef.bundle.remove(property.name)
         }
         setNotification?.invoke(value)
      }
   }
}
