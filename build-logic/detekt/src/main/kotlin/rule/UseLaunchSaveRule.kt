package rule

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression

class UseLaunchSaveRule(config: Config) : Rule(config) {


    override val issue: Issue = Issue(
        id = "UseLaunchSave",
        description = "Must use a custom launch extension .launchSave { ... }",
        severity = Severity.CodeSmell,
        debt = Debt.FIVE_MINS,
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val callee = expression.calleeExpression?.text
        if (callee == "launch") {
            // Check if it's not already using launchSave
            val fullText = expression.parent?.text ?: expression.text
            if (!fullText.contains("launchSave")) {
                report(
                    CodeSmell(
                        issue = issue,
                        entity = Entity.from(expression),
                        message = "Use .launchSave instead of .launch for better error handling!"
                    )
                )
            }
        }
    }

}
