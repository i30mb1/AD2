plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.androidGradlePlugin)
    implementation(libs.kotlinGradlePlugin)
    implementation("org.jetbrains.kotlinx:kover-gradle-plugin:0.7.4")
    implementation(project(":build-extensions"))
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
