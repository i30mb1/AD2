import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    androidApp()
    kotlinKapt()
}

android {
    defaultConfig {
        applicationId = Apps.applicationId
        versionCode = Apps.versionCode
        versionName = Apps.versionName

        resourceConfigurations.addAll(listOf("ru", "en"))

        javaCompileOptions {
            annotationProcessorOptions {
                argument("room.incremental", "true")
                argument("room.expandProjection", "true")
            }
        }
    }

    signingConfigs {
        getByName("debug") { /* automatic signs with debug key*/ }
        create("release") {
            storeFile = file(gradleLocalProperties(rootDir).getProperty("storeFile"))
            storePassword = gradleLocalProperties(rootDir).getProperty("storePassword")
            keyAlias = gradleLocalProperties(rootDir).getProperty("keyAlias")
            keyPassword = gradleLocalProperties(rootDir).getProperty("keyPassword")
        }
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
        getByName("release") {
            isMinifyEnabled = true
            isDebuggable = false
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            resValue("string", "app_name2", "AD2")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
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
    addBaseDependencies()
    addTestDependencies()

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