plugins {
    id("com.diffplug.spotless")
}

val disabledRules = mapOf(
    "ktlint_standard_property-naming" to "disabled",
    "ktlint_standard_backing-property-naming" to "disabled",
    "ktlint_standard_function-naming" to "disabled",
    "ktlint_standard_package-name" to "disabled",
    "ktlint_standard_filename" to "disabled",
    "ktlint_standard_no-empty-file" to "disabled",
    "ktlint_standard_multiline-expression-wrapping" to "disabled",
)

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**")
        ktlint().editorConfigOverride(disabledRules)
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude("**/build/**")
        ktlint().editorConfigOverride(disabledRules)
    }
}
