package n7.ad2.rules

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import n7.ad2.rules.issues.WrongUsingAttrDetector

class IssueRegistry : IssueRegistry() {

    override val api: Int = CURRENT_API

    override val vendor: Vendor = Vendor(
        vendorName = "AD2 Project",
        identifier = "n7.ad2.rules",
        feedbackUrl = "https://github.com/n7/AD2/issues",
    )

    override val issues: List<Issue>
        get() = listOf(
            WrongUsingAttrDetector.ISSUE,
        )
}
