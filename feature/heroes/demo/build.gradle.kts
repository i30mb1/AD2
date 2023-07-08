plugins {
    id("convention.android-application")
    id("n7.plugins.kotlin-kapt")
}

android {
    namespace = "$applicationID.heroes.demo"

    buildFeatures {
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
            resValue("string", "app_name", "AD2-Heroes")
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

    implementation(project(Module.Feature.Heroes.ui))
    implementation(project(Module.Feature.Heroes.impl))

    kapt(libs.daggerAnnotation)
}
