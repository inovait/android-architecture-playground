package si.inova.androidarchitectureplayground.showkase

import android.content.Context
import com.airbnb.android.showkase.annotation.ShowkaseRoot
import com.airbnb.android.showkase.annotation.ShowkaseRootModule
import com.airbnb.android.showkase.models.Showkase
import me.tatarka.inject.annotations.Inject
import si.inova.androidarchitectureplayground.ui.showkase.ShowkaseLauncher
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@ContributesBinding(AppScope::class)
class ShowkaseLauncherImpl @Inject constructor() : ShowkaseLauncher {
   override fun launch(context: Context) {
      context.startActivity(Showkase.getBrowserIntent(context))
   }
}

@ShowkaseRoot
class MyRootModule : ShowkaseRootModule
