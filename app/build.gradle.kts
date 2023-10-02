import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("convention.android-application")
    id("n7.plugins.kotlin-kapt")
}

android {
    namespace = applicationID
    defaultConfig {
        applicationId = applicationID
        versionCode = getVersionCode()
        versionName = getVersionName()

        resourceConfigurations.addAll(listOf("ru", "en"))
        setProperty("archivesBaseName", "$applicationId-$versionName")
    }

    signingConfigs {
        getByName("debug") { /* automatic signs with debug key*/ }
        create("release") {
            val properties = gradleLocalProperties(rootDir)
            if (properties.containsKey("SIGNING_FILE")) {
                storeFile = file("key.jks")
                storePassword = properties.getProperty("STORE_PASSWORD")
                keyAlias = properties.getProperty("KEY_ALIAS")
                keyPassword = properties.getProperty("KEY_PASSWORD")
            }
        }
    }

    buildFeatures {
        buildConfig = true
        resValues = true
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "/debug"
            signingConfig = signingConfigs.getByName("debug")
            resValue("string", "app_name", "AD2-D")
        }
        create("benchmark") {
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
        release {
            isMinifyEnabled = true
            isDebuggable = false
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

//    flavorDimensions += "environment"
//    productFlavors {
//        create("dev") {
//            dimension = "environment"
//            applicationIdSuffix = ".dev"
//            versionNameSuffix = "/dev"
//        }
//        create("prod") {
//            dimension = "environment"
//            applicationIdSuffix = ".prod"
//            versionNameSuffix = "/prod"
//        }
//    }
//    variantFilter {
//        if (name == "devDebug" || name == "devRelease") ignore = true
//    }

//    setDynamicFeatures(mutableSetOf(":feature_streams"))

    testOptions {
        unitTests.apply {
            isIncludeAndroidResources = true
        }
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.ticker)
    implementation(libs.viewPager2)
    implementation(libs.jsoup)
    implementation(libs.instantApps)
    implementation(libs.workManager)
    implementation(libs.performance)
    implementation(libs.moshi)

    kapt(libs.daggerAnnotation)

    implementation(project(Module.Core.android)) { because("!") }
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.repositories))
    implementation(project(Module.Core.database))
    implementation(project(Module.Core.navigator))
    implementation(project(Module.Core.appPreference))
    implementation(project(Module.Core.retrofit))
    implementation(project(Module.Core.updateManager))
    implementation(project(Module.Core.spanParser))
    implementation(project(Module.Core.yandexMetrics))
    implementation(project(Module.Core.commonApplication))

    implementation(project(Module.Feature.streams))
    implementation(project(Module.Feature.Heroes.ui))
    implementation(project(Module.Feature.Heroes.impl))
    implementation(project(Module.Feature.Heroes.wiring))
    implementation(project(Module.Feature.Items.ui))
    implementation(project(Module.Feature.games))
    implementation(project(Module.Feature.tournaments))
    implementation(project(Module.Feature.News.ui))
    implementation(project(Module.Feature.drawer))
    implementation(project(Module.Feature.HeroPage.ui))
    implementation(project(Module.Feature.itemPage))
    implementation(project(Module.Feature.settings))

    androidTestImplementation(libs.espresso)
    androidTestImplementation(libs.testRunner)
    androidTestImplementation(libs.testRules)
    androidTestImplementation(libs.testJunitKtx)
}
