plugins {
    id("convention.android-application")
    id("convention.compose")
    id("n7.plugins.kotlin-kapt")
}

android {
    namespace = "$applicationID.xo.demo"

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
            resValue("string", "app_name", "AD2-Xo")
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

    implementation(projects.feature.games.xo.ui)
    implementation(projects.feature.games.xo.domain.impl)
    implementation(projects.feature.games.xo.domain.wiring)

    kapt(libs.daggerAnnotation)

    androidTestImplementation(libs.test.junit.kotlin)
    androidTestImplementation(libs.test.fragment)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.test.espresso)
    androidTestImplementation(libs.testCoreKtx)
    androidTestImplementation(libs.testRules)

    testImplementation(libs.test.junit.kotlin)
    testImplementation(libs.test.fragment)
    testImplementation(libs.test.runner)
    testImplementation(libs.test.espresso)
    testImplementation(libs.test.robolectric)
    testImplementation(libs.testCoreKtx)
    testImplementation(libs.testRules)
}
