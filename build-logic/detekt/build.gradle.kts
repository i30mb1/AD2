plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.22.0")
    implementation("io.gitlab.arturbosch.detekt:detekt-api:1.22.0")

    testImplementation("io.gitlab.arturbosch.detekt:detekt-test:1.22.0")
    testImplementation(libs.bundles.test)
}