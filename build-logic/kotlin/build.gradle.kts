plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.androidGradlePlugin)
    implementation(libs.kotlinGradlePlugin)
    implementation(libs.kspGradlePlugin)
    implementation(libs.kover)
    implementation(libs.kotlin.power.assert)
    implementation(libs.kotlin.serialization.plugin)
    implementation(projects.buildExtensions)
}

gradlePlugin {
    plugins {
        register("KotlinKaptPlugin") {
            id = "n7.plugins.kotlin-kapt"
            implementationClass = "n7.plugins.KotlinKaptPlugin"
            displayName = "Kotlin Kapt Plugin"
        }
        register("KotlinKspPlugin") {
            id = "n7.plugins.kotlin-ksp"
            implementationClass = "n7.plugins.KotlinKspPlugin"
            displayName = "Kotlin KSP Plugin"
        }
    }
}
