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
        create("release") {
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
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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
}

