import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.detektPlugin)
    implementation(libs.detektApi)
    implementation(libs.detektCli)

    implementation(project(":extensions"))

    testImplementation(libs.detektTest)
    testImplementation(libs.bundles.test)
}

// using to avoid compile Kotlin with old version
// https://handstandsam.com/2022/04/13/using-the-kotlin-dsl-gradle-plugin-forces-kotlin-1-4-compatibility/
afterEvaluate {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            apiVersion = "1.7"
            languageVersion = "1.7"
        }
    }
}