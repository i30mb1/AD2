import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

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

        javaCompileOptions {
            annotationProcessorOptions {
                argument("room.incremental", "true")
                argument("room.expandProjection", "true")
            }
        }
    }

    buildFeatures {
        dataBinding = true
    }

    signingConfigs {
        create("releaseConfig") {
            storeFile = file(gradleLocalProperties(rootDir).getProperty("storeFile"))
            storePassword = gradleLocalProperties(rootDir).getProperty("storePassword")
            keyAlias = gradleLocalProperties(rootDir).getProperty("keyAlias")
            keyPassword = gradleLocalProperties(rootDir).getProperty("keyPassword")
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

    setDynamicFeatures(mutableSetOf(":feature_streams"))

    testOptions {
        unitTests.apply {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    debugImplementation(Lib.debugDB)
    implementation(Lib.ticker)
    implementation(Lib.sliding)
    implementation(Lib.paging3)
    implementation(Lib.picasso)
    implementation(Lib.picassoTrans)
    implementation(Lib.viewPager2)
    implementation(Lib.picassoTrans2)
    implementation(Lib.jsoup)
    implementation(Lib.firebaseAnal)
    implementation(Lib.instantApps)
    
    lintChecks(project(":rules"))

    addBaseDependencies()
    addTestDependencies()
}
