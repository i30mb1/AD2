package ad2.n7.rules.issues

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.LayoutDetector
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.TextFormat
import com.android.tools.lint.detector.api.XmlContext
import org.w3c.dom.Attr

// class that is able to find background attribute in xml files that not start with "?" mark
@Suppress("UnstableApiUsage")
class WrongUsingAttrDetector : LayoutDetector() {

    override fun getApplicableAttributes(): Collection<String>? = listOf(
        "background", "foreground", "src", "textColor", "tint", "color",
        "textColorHighlight", "textColorHint", "textColorLink", "shadowColor", "srcCompat"
    )

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        super.visitAttribute(context, attribute)
        if (attribute.value.startsWith("#") || attribute.value.startsWith("@color/")) {
            context.report(
                ISSUE,
                context.getLocation(attribute.ownerElement),
                ISSUE.getExplanation(TextFormat.RAW)
            )
        }
    }

    companion object {

        val ISSUE: Issue = Issue
            .create(
                id = "DirectColorUse",
                briefDescription = "Direct color used",
                explanation = """
                 Avoid direct use of colors in XML files. This will cause issues with different theme (eg. night) support
            """.trimIndent(),
                category = Category.CORRECTNESS,
                priority = 9,
                severity = Severity.ERROR,
                androidSpecific = true,
                implementation = Implementation(WrongUsingAttrDetector::class.java, Scope.RESOURCE_FILE_SCOPE)
            )
    }
}
