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
                argument("room.incremental","true")
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

    dynamicFeatures = mutableSetOf(":feature_streams")

    testOptions {
        unitTests.apply {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    debugImplementation(Lib.debugDB)
    api(Lib.ticker)
    api(Lib.sliding)
    api(Lib.retrofit)
    api(Lib.gsonConverter)
    api(Lib.workManager)
    api(Lib.workManagerKotlin)
    api(Lib.room)
    api(Lib.roomKtx)
    kapt(Lib.roomAnnotation)
    api(Lib.paging)
    api(Lib.material)
    api(Lib.coreKtx)
    api(Lib.constraintLayout)
    api(Lib.coil)
    api(Lib.picasso)
    api(Lib.picassoTrans)
    api(Lib.picassoTrans2)
    api(Lib.jsoup)
    api(Lib.firebaseAnal)
    api(Lib.playCore)
    api(Lib.playCoreKtx)
    api(Lib.coroutinesLifecycle)
    api(Lib.coroutinesLivedata)
    api(Lib.coroutinesViewmodel)
    api(Lib.fragmentKtx)
    api(Lib.activityKtx)
    implementation(Lib.dagger)
    kapt(Lib.daggerAnnotation)
    compileOnly(Lib.daggerAssisted)
    kapt(Lib.daggerAssistedAnnotation)
    implementation(Lib.preference)
    implementation(Lib.preferenceKtx)
    implementation(Lib.springAnimation)
    implementation(Lib.springAnimationKtx)
    implementation(Lib.instantApps)
    implementation(Lib.viewPager2)
    implementation(Lib.exoPlayer)

    implementation(Lib.moshi)
    kapt(Lib.moshiCodegen)
    
    lintChecks(project(":rules"))

    addTestDependencies()
}
