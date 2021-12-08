plugins {
    androidApp()
    kotlinAndroid()
    kotlinKapt()
    id("com.github.plnice.canidropjetifier") version "0.5"
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
        create("releaseConfig") {
//            storeFile = file(gradleLocalProperties(rootDir).getProperty("storeFile"))
//            storePassword = gradleLocalProperties(rootDir).getProperty("storePassword")
//            keyAlias = gradleLocalProperties(rootDir).getProperty("keyAlias")
//            keyPassword = gradleLocalProperties(rootDir).getProperty("keyPassword")
        }
    }

    buildTypes {
        getByName("debug") {
            resValue("string", "PORT_NUMBER", "8081")
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("releaseConfig")
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

    implementation(project(Module.Core.android))
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
}
