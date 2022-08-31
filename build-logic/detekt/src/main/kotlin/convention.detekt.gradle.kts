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

    // todo вот бы файл использовать который лежал бы прям в этой папке...
    config.setFrom(files("$projectDir\\build-logic\\detekt\\detekt-config.yml", PathValidation.EXISTS))
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
