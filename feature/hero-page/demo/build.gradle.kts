plugins {
    id("convention.android-application")
    id("n7.plugins.kotlin-ksp")
}

android {
    namespace = "$applicationID.hero.page.demo"

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
            resValue("string", "app_name", "AD2-Hero-Page")
        }
    }
}

dependencies {
    implementation(projects.core.commonAndroid)
    implementation(projects.core.navigator)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.retrofit)
    implementation(projects.core.appPreference)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.commonApplication)

    implementation(projects.feature.heroPage.ui)
    implementation(projects.feature.heroPage.domain.impl)
    implementation(projects.feature.heroPage.domain.wiring)

    ksp(libs.daggerAnnotation)
}