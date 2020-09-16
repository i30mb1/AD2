package ad2.n7.rules

import ad2.n7.rules.issues.WrongUsingAttrDetector
import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

class IssueRegistry : IssueRegistry() {

    override val api: Int = CURRENT_API

    override val issues: List<Issue>
        get() = listOf(
            WrongUsingAttrDetector.ISSUE
        )
}
