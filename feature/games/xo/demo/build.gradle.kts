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
    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.commonApplication))
    implementation(project(Module.Core.navigator))

    implementation(project(Module.Feature.Xo.ui))
    implementation(project(Module.Feature.Xo.impl))
    implementation(project(Module.Feature.Xo.wiring))

    kapt(libs.daggerAnnotation)
}
