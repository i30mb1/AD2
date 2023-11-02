plugins {
    id("convention.android-application")
    id("convention.compose")
    id("n7.plugins.kotlin-kapt")
}

android {
    namespace = "$applicationID.games.demo"

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
            resValue("string", "app_name", "AD2-Game")
        }
    }
}

dependencies {
    implementation(project(Module.Core.android))
    implementation(project(Module.Core.navigator))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.retrofit))
    implementation(project(Module.Core.appPreference))
    implementation(project(Module.Core.database))
    implementation(project(Module.Core.spanParser))
    implementation(project(Module.Core.updateManager))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.commonApplication))

    implementation(project(Module.Feature.Heroes.impl))
    implementation(project(Module.Feature.Heroes.wiring))

    implementation(project(Module.Feature.Games.ui))
    implementation(project(Module.Feature.Games.impl))
    implementation(project(Module.Feature.Games.wiring))

    kapt(libs.daggerAnnotation)
}
