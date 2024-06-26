import gradle.kotlin.dsl.accessors._caaef686956ef05d8c7d73205bf1c4b7.detekt
import gradle.kotlin.dsl.accessors._caaef686956ef05d8c7d73205bf1c4b7.detektPlugins
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("io.gitlab.arturbosch.detekt")
}

val detektAll = tasks.register<Detekt>("detektAll") {
    description = "Runs over whole code base without the starting overhead for each module."
    group = "n7"
    parallel = true

    allRules = false
    ignoreFailures = true
    setSource(files(projectDir))

    config.setFrom(getResource("config.yml"))
    buildUponDefaultConfig = false

    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")
    exclude("**/test/**")
    exclude("**/androidTest/**")
    reports {
        xml.required.set(false)
        sarif.required.set(false)
    }

    jvmTarget = "11"
}

dependencies {
    detektPlugins(catalog.findLibrary("detektFormatting").get())
    detekt("build-logic:detekt")
}
