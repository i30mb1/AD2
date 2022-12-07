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

    val path = "$projectDir\\core\\detekt\\src\\main\\resources\\config.yml"
    config.setFrom(files(path))
    buildUponDefaultConfig = false

    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")
    exclude("**/test/**")
    reports {
        xml.required.set(false)
        sarif.required.set(false)
    }

    jvmTarget = "11"
}

dependencies {
    detekt(project(":core:detekt"))
}
