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
import androidx.fragment.app.FragmentManager
import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.screens.ScreenCKey
import si.inova.kotlinova.navigation.instructions.goBack
import si.inova.kotlinova.navigation.instructions.navigateTo
import java.util.UUID

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

@Parcelize
data class TestFragmentKey(val arg: String, override val tag: String = UUID.randomUUID().toString()) : FragmentScreenKey()

class TestFragmentScreen(scopeExitListener: ScopeExitListener) : FragmentScreen<TestFragmentKey>(scopeExitListener) {
   override fun createFragment(key: TestFragmentKey, fragmentManager: FragmentManager): Fragment {
      return TestFragment().apply {
         arguments = bundleOf("TEST" to key.arg)
      }
   }
}
