plugins {
    androidApp()
    kotlinAndroid()
    kotlinAndroidExt()
    kotlinKapt()
}

android {
    compileSdkVersion(Apps.compileSdk)
    defaultConfig {
        vectorDrawables.useSupportLibrary = true
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
            storeFile = file("C:\\Users\\i30mb1\\AndroidProjects\\key111111.jks")
            storePassword = "111111"
            keyAlias = "key"
            keyPassword = "111111"
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
//     Animated TextView
    api("com.robinhood.ticker:ticker:1.2.2")
    // Navigation
    api("com.yarolegovich:sliding-root-nav:1.1.0")
    // cmd ipconfig основной шлюз + :8080 (как узнать страничку для браузера)
    debugImplementation("com.amitshekhar.android:debug-db:1.0.1")
    // Retrofit
    api("com.squareup.retrofit2:retrofit:2.4.0")
    api("com.squareup.retrofit2:converter-gson:2.3.0")
    // WorkManager
    api("androidx.work:work-runtime:2.0.1")
    // Room components
    api("androidx.room:room-runtime:2.0.0")
    kapt("androidx.room:room-compiler:2.0.0")
    // Paging
    api("androidx.paging:paging-runtime:2.0.0")
//     Android
    api("com.google.android.material:material:1.0.0")
    api("androidx.constraintlayout:constraintlayout:1.1.3")
    // Picasso
    api("com.squareup.picasso:picasso:2.71828")
    api("jp.wasabeef:picasso-transformations:2.2.1")
    api("jp.co.cyberagent.android.gpuimage:gpuimage-library:1.4.1")
    // Jsoup для разбора html
    api("org.jsoup:jsoup:1.11.2")
    //поддержка jsoup для старых устройств

    api("com.google.firebase:firebase-analytics:17.3.0")

//    api("com.google.android.exoplayer:exoplayer-core:2.10.2")

    // auto update
    api("com.google.android.play:core:1.6.1")

//    api("androidx.core:core-ktx:1.2.0")
//    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
//    api("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0")
//    api("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
//    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
//    api("androidx.fragment:fragment-ktx:1.2.4")
//    api("androidx.activity:activity-ktx:1.1.0")

}

