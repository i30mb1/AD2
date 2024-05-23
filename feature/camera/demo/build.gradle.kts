plugins {
    id("convention.android-application")
    id("convention.compose")
    id("n7.plugins.kotlin-kapt")
}

android {
    namespace = "$applicationID.camera.demo"

    buildFeatures {
        buildConfig = true
        resValues = true
    }
    signingConfigs {
        getByName("debug") { /* automatic signs with debug key*/ }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            resValue("string", "app_name", "AD2-Camera")
        }
    }
}

dependencies {
    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.commonApplication)
    implementation(projects.core.navigator)

    implementation(projects.feature.camera.ui)
    implementation(projects.feature.camera.domain.impl)
    implementation(projects.feature.camera.domain.wiring)

    androidTestImplementation(libs.test.junit.kotlin)
    androidTestImplementation(libs.test.fragment)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.test.espresso)
    androidTestImplementation(libs.test.uiautomator)
    androidTestImplementation(libs.test.core)
    androidTestImplementation(libs.test.rules)

    kapt(libs.daggerAnnotation)
}
