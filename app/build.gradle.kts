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

    buildFeatures {
        dataBinding = true
        viewBinding = true
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
    implementation(project(Module.Feature.streams))
}
