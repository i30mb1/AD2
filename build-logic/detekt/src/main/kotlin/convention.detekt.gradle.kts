import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("io.gitlab.arturbosch.detekt")
}

val detektAll = tasks.register<Detekt>("detektAll") {
    description = "Runs over whole code base without the starting overhead for each module."
    group = "n7"
    parallel = true

    allRules = false
    setSource(files(projectDir))

    val path = "$projectDir\\build-logic\\detekt\\src\\main\\resources\\config.yml"
    println(path)
    config.setFrom(files(path))
    buildUponDefaultConfig = false

    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")
    reports {
        xml.required.set(false)
        sarif.required.set(false)
    }

    jvmTarget = "11"
}
