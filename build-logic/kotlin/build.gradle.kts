plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.androidGradlePlugin)
    implementation(libs.kotlinGradlePlugin)
    implementation(libs.kover)
    implementation(projects.buildExtensions)
}

gradlePlugin {
    plugins {
        register("KotlinKaptPlugin") {
            id = "n7.plugins.kotlin-kapt"
            implementationClass = "n7.plugins.KotlinKaptPlugin"
            displayName = "Kotlin Kapt Plugin"
        }
    }
}
