package si.inova.androidarchitectureplayground.showkase

import android.content.Context
import com.airbnb.android.showkase.annotation.ShowkaseRoot
import com.airbnb.android.showkase.annotation.ShowkaseRootModule
import me.tatarka.inject.annotations.Inject
import si.inova.androidarchitectureplayground.ui.showkase.ShowkaseLauncher
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@ContributesBinding(AppScope::class)
class ShowkaseLauncherImpl @Inject constructor() : ShowkaseLauncher {
   override fun launch(context: Context) {
      // TODO uncomment this when you have at least one preview marked with @ShowkaseComposable
      // context.startActivity(Showkase.getBrowserIntent(context))

      // To enable showkase, you need to put a button somewhere that is only visible on debug builds and make button
      // call ShowkaseLauncher.launch() when clicked.
   }
}

@ShowkaseRoot
class MyRootModule : ShowkaseRootModule
