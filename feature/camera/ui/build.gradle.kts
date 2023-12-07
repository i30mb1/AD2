plugins {
    id("convention.android-library")
    id("convention.compose")
    id("n7.plugins.kotlin-kapt")
    id("org.jetbrains.kotlinx.kotlin-deeplearning-gradle-plugin") version "0.6.0-alpha-1"
}

dependencies {
    implementation(libs.bundles.camera)
    implementation(libs.bundles.kotlinDL)

    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.navigator)
    implementation(projects.feature.camera.domain.api)

    kapt(libs.daggerAnnotation)
}

downloadKotlinDLModels {
    models.add("UltraFace320")
    overwrite = false
}
