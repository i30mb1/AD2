plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.androidGradlePlugin)
    implementation(libs.kotlinGradlePlugin)
    implementation("org.jacoco:org.jacoco.core:0.8.8")
    implementation(project(":extensions"))
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
