package si.inova.androidarchitectureplayground.detekt

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.isPublic

class LogActionDetektRule(ruleSetConfig: Config) : Rule(ruleSetConfig) {
   override val issue: Issue
      get() = Issue("viewModelLogAction", Severity.Minor, "You use LogAction in ViewModels", Debt.FIVE_MINS)

   private var inViewModel: Boolean = false
   private var inPublicViewModelFunction: Boolean = false
   private var functionName: String = ""

   override fun visitClass(klass: KtClass) {
      inViewModel = klass.name?.endsWith("ViewModel") == true
      super.visitClass(klass)
   }

   override fun visitNamedFunction(function: KtNamedFunction) {
      inPublicViewModelFunction = inViewModel && function.isPublic
      functionName = function.name.orEmpty()

      super.visitNamedFunction(function)
   }

   override fun visitCallExpression(expression: KtCallExpression) {
      if (inPublicViewModelFunction) {
         if (expression.calleeExpression?.text != "logAction") {
            report(CodeSmell(issue, Entity.from(expression), "You must call logAction at the start of $functionName"))
         }

         inPublicViewModelFunction = false
      }

      super.visitCallExpression(expression)
   }
}
