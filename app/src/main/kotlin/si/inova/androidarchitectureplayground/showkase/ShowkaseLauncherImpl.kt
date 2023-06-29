package si.inova.androidarchitectureplayground.showkase

import android.content.Context
import com.airbnb.android.showkase.annotation.ShowkaseRoot
import com.airbnb.android.showkase.annotation.ShowkaseRootModule
import com.airbnb.android.showkase.models.Showkase
import com.squareup.anvil.annotations.ContributesBinding
import si.inova.androidarchitectureplayground.common.di.ApplicationScope
import si.inova.androidarchitectureplayground.ui.showkase.ShowkaseLauncher
import javax.inject.Inject

@ContributesBinding(ApplicationScope::class)
class ShowkaseLauncherImpl @Inject constructor() : ShowkaseLauncher {
   override fun launch(context: Context) {
      context.startActivity(Showkase.getBrowserIntent(context))
   }
}

@ShowkaseRoot
class MyRootModule : ShowkaseRootModule
