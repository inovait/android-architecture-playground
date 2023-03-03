package si.inova.androidarchitectureplayground.migration

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Keep
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import si.inova.androidarchitectureplayground.navigation.instructions.goBack
import si.inova.androidarchitectureplayground.navigation.instructions.navigateTo
import si.inova.androidarchitectureplayground.screens.ScreenCKey

@Keep
class TestFragment : Fragment() {
   @SuppressLint("SetTextI18n")
   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
      return LinearLayout(requireContext()).apply {
         val text = TextView(requireContext())
         text.text = "Got argument: ${arguments?.getString("TEST")}, saved state: $savedInstanceState"

         val buttonA = Button(requireContext())
         buttonA.text = "Go back"
         buttonA.setOnClickListener {
            val navigator = (requireActivity() as NavigatorActivity).navigator
            navigator.goBack()
         }

         val buttonB = Button(requireContext())
         buttonB.text = "Open C"
         buttonB.setOnClickListener {
            val navigator = (requireActivity() as NavigatorActivity).navigator
            navigator.navigateTo(ScreenCKey(1, "a"))
         }

         orientation = LinearLayout.VERTICAL

         addView(text)
         addView(buttonA)
         addView(buttonB)
      }
   }
}

fun TestFragmentKey(arg: String) = FragmentScreenKey(
   "si.inova.androidarchitectureplayground.migration.TestFragment",
   bundleOf("TEST" to arg)
)
