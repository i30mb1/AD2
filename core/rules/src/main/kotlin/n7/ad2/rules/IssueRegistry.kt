package n7.ad2.rules

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import n7.ad2.rules.issues.WrongUsingAttrDetector

class IssueRegistry : IssueRegistry() {

    override val api: Int = CURRENT_API

    override val issues: List<Issue>
        get() = listOf(
            WrongUsingAttrDetector.ISSUE
        )
}
