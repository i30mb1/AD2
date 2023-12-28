import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("convention.android-application")
    id("n7.plugins.kotlin-kapt")
}

android {
    lint {
        abortOnError = true
    }
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
        create("qa") {
            matchingFallbacks += "release"
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
    implementation(libs.performancePlayServices)
    implementation(libs.moshi)
    implementation(libs.androidStartup)

    kapt(libs.daggerAnnotation)

    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.coroutines)
    implementation(projects.core.repositories)
    implementation(projects.core.database)
    implementation(projects.core.navigator)
    implementation(projects.core.appPreference)
    implementation(projects.core.retrofit)
    implementation(projects.core.updateManager)
    implementation(projects.core.spanParser)
    implementation(projects.core.logger.yandex)
    implementation(projects.core.commonApplication)

    implementation(projects.feature.streams)
    implementation(projects.feature.heroes.ui)
    implementation(projects.feature.heroes.domain.impl)
    implementation(projects.feature.heroes.domain.wiring)

    implementation(projects.feature.items.ui)
    implementation(projects.feature.items.domain.impl)
    implementation(projects.feature.items.domain.wiring)

    implementation(projects.feature.games.mix.ui)
    implementation(projects.feature.games.mix.domain.impl)
    implementation(projects.feature.games.mix.domain.wiring)

    implementation(projects.feature.tournaments)

    implementation(projects.feature.news.ui)
    implementation(projects.feature.news.domain.impl)
    implementation(projects.feature.news.domain.wiring)

    implementation(projects.feature.drawer)

    implementation(projects.feature.heroPage.ui)

    implementation(projects.feature.itemPage)
    implementation(projects.feature.settings)

    androidTestImplementation(libs.espresso)
    androidTestImplementation(libs.testRunner)
    androidTestImplementation(libs.testRules)
    androidTestImplementation(libs.testJunitKtx)
}
