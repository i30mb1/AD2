plugins {
    id("convention.android-application")
    id("n7.plugins.kotlin-kapt")
}

android {
    namespace = "$applicationID.news.demo"

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
            resValue("string", "app_name", "AD2-News")
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
    implementation(projects.core.database)
    implementation(projects.core.spanParser)
    implementation(projects.core.updateManager)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.commonApplication)

    implementation(projects.feature.news.ui)
    implementation(projects.feature.news.domain.impl)
    implementation(projects.feature.news.domain.wiring)

    kapt(libs.daggerAnnotation)
}
