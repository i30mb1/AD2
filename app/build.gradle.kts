plugins {
    application()
    kapt()
}

android {
    defaultConfig {
        applicationId = "n7.ad2"
        versionCode = 555
        versionName = "555"

        resourceConfigurations.addAll(listOf("ru", "en"))
    }

    signingConfigs {
        getByName("debug") { /* automatic signs with debug key*/ }
//        create("release") {
//            storeFile = file(gradleLocalProperties(rootDir).getProperty("storeFile"))
//            storePassword = gradleLocalProperties(rootDir).getProperty("storePassword")
//            keyAlias = gradleLocalProperties(rootDir).getProperty("keyAlias")
//            keyPassword = gradleLocalProperties(rootDir).getProperty("keyPassword")
//        }
    }

    buildFeatures {
        buildConfig = true
        resValues = true
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            signingConfig = signingConfigs.getByName("debug")
            resValue("string", "app_name2", "AD2(debug)")
        }
//        getByName("release") {
//            isMinifyEnabled = true
//            isDebuggable = false
//            isShrinkResources = true
//            signingConfig = signingConfigs.getByName("release")
//            resValue("string", "app_name2", "AD2")
//            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
//        }
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }
        create("prod") {
            dimension = "environment"
            applicationIdSuffix = ".prod"
            versionNameSuffix = "-prod"
        }
    }
    variantFilter {
        if (name == "devDebug" || name == "devRelease") ignore = true
    }

//    setDynamicFeatures(mutableSetOf(":feature_streams"))

    testOptions {
        unitTests.apply {
            isIncludeAndroidResources = true
        }
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Lib.ticker)
    implementation(Lib.viewPager2)
    implementation(Lib.jsoup)
    implementation(Lib.firebaseAnal)
    implementation(Lib.instantApps)
    implementation(Lib.workManagerKotlin)
    addBaseDependencies()

    kapt(Lib.daggerAnnotation)

    lintChecks(project(Module.Core.rules))

    implementation(project(Module.Core.android)) { because("!") }
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.repositories))
    implementation(project(Module.Core.database))
    implementation(project(Module.Core.provider))
    implementation(project(Module.Core.appPreference))

    implementation(project(Module.Feature.streams))
    implementation(project(Module.Feature.heroes))
    implementation(project(Module.Feature.items))
    implementation(project(Module.Feature.games))
    implementation(project(Module.Feature.tournaments))
    implementation(project(Module.Feature.news))
    implementation(project(Module.Feature.drawer))
    implementation(project(Module.Feature.heroPage))
    implementation(project(Module.Feature.itemPage))
}