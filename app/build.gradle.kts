import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    androidApp()
    kotlinAndroid()
    kotlinAndroidExt()
    kotlinKapt()
}

android {
    compileSdkVersion(Apps.compileSdk)
    defaultConfig {
        applicationId = Apps.applicationId
        minSdkVersion(Apps.minSdk)
        targetSdkVersion(Apps.targetSdk)
        versionCode = Apps.versionCode
        versionName = Apps.versionName
    }

    dataBinding {
        isEnabled = true
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    tasks.withType < org.jetbrains.kotlin.gradle.tasks.KotlinCompile > {
        kotlinOptions {
            noStdlib = true
            jvmTarget = JavaVersion.VERSION_1_8.toString()
            freeCompilerArgs = listOf("-Xallow-result-return-type")
        }
    }

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
    api(Lib.coroutines)
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

    addTestDependencies()
}
