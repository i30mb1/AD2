import org.gradle.api.artifacts.dsl.DependencyHandler

object Apps {
    const val compileSdk = 30
    const val buildToolsSdk = "30.0.2"
    const val applicationId = "n7.ad2"
    const val minSdkVersion = 23
    const val targetSdkVersion = 30
    const val versionCode = 555
    const val versionName = "555"
}

object Versions {
    const val gradlePlugin = "7.0.0"
    const val lint = "27.1.3" // gradlePlugin + 23
    const val workManager = "2.7.0-alpha05"
    const val moshi = "1.12.0"
    const val room = "2.3.0"
    const val kotlin = "1.5.30"
    const val dagger = "2.38.1"
    const val lifecycle = "2.4.0-alpha01"
    const val retrofit = "2.7.1"
    const val coroutines = "1.5.2"
    const val dataStore = "1.0.0"
    const val exoPlayer = "2.15.1"
}

object Lib {
    const val dataStorePref = "androidx.datastore:datastore-preferences:${Versions.dataStore}"
    const val recyclerView = "androidx.recyclerview:recyclerview:1.2.0-beta02"
    const val kotlinReflection = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    const val ticker = "com.robinhood.ticker:ticker:1.2.2"
    const val sliding = "com.yarolegovich:sliding-root-nav:1.1.0"
    const val debugDB = "com.amitshekhar.android:debug-db:1.0.1" // cmd ipconfig основной шлюз + :8080 (как узнать страничку для браузера)
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitMoshiConverter = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
    const val retrofitInterceptor = "com.squareup.okhttp3:logging-interceptor:4.3.1"
    const val retrofitScalars = "com.squareup.retrofit2:converter-scalars:2.9.0"
    const val paging3 = "androidx.paging:paging-runtime:3.0.0"
    const val material = "com.google.android.material:material:1.2.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"
    const val coil = "io.coil-kt:coil:0.13.0"
    const val picasso = "com.squareup.picasso:picasso:2.71828"
    const val picassoTrans = "jp.wasabeef:picasso-transformations:2.2.1"
    const val picassoTrans2 = "jp.co.cyberagent.android.gpuimage:gpuimage-library:1.4.1"
    const val jsoup = "org.jsoup:jsoup:1.11.2" // Jsoup для разбора html
    const val firebaseAnal = "com.google.firebase:firebase-analytics:17.3.0"
    const val playCore = "com.google.android.play:core:1.8.0" // auto update + rateMe
    const val playCoreKtx = "com.google.android.play:core-ktx:1.8.1"
    const val coreKtx = "androidx.core:core-ktx:1.6.0-alpha02" // Write more concise, idiomatic Kotlin code.
    const val jsonSimple = "com.googlecode.json-simple:json-simple:1.1.1"
    const val springAnimation = "androidx.dynamicanimation:dynamicanimation:1.0.0"
    const val springAnimationKtx = "androidx.dynamicanimation:dynamicanimation-ktx:1.0.0-alpha03"
    const val instantApps = "com.google.android.gms:play-services-instantapps:17.0.0"
    const val viewPager2 = "androidx.viewpager2:viewpager2:1.0.0"

    const val exoPlayerCore = "com.google.android.exoplayer:exoplayer-core:${Versions.exoPlayer}"
    const val exoPlayerHls = "com.google.android.exoplayer:exoplayer-hls:${Versions.exoPlayer}"
    const val exoPlayerUi = "com.google.android.exoplayer:exoplayer-ui:${Versions.exoPlayer}"
    const val exoPlayerMediaSession = "com.google.android.exoplayer:extension-mediasession:${Versions.exoPlayer}"

    const val workManager = "androidx.work:work-runtime:2.0.1"
    const val workManagerKotlin = "androidx.work:work-runtime-ktx:${Versions.workManager}"
    const val workGCM = "androidx.work:work-gcm:${Versions.workManager}"


    // --- Preference ---
    const val preference = "androidx.preference:preference:1.1.1"
    const val preferenceKtx = "androidx.preference:preference-ktx:1.1.1"

    // --- Room ---
    const val room = "androidx.room:room-runtime:${Versions.room}"
    const val roomAnnotation = "androidx.room:room-compiler:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}" // kotlin Extensions and Coroutines support for Room

    // --- Coroutines ---
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}" // for testing coroutines
    const val coroutinesLifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}" // lifecycleScope + launchWhenResumed and ets.
    const val coroutinesLivedata = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}" // liveData (LiveData + coroutines)
    const val coroutinesViewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}" // viewModelScope + savedStateHandle
    const val lifecycleAnnotation = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}" // that's only needed if you have lifecycle-related annotations in your code, specifically @OnLifecycleEvent
    const val fragmentKtx = "androidx.fragment:fragment-ktx:1.3.0-alpha06" // easy fragment transaction + by viewModels()
    const val activityKtx = "androidx.activity:activity-ktx:1.1.0" // on BackPress support for Fragment

    // --- Moshi ---
    const val moshi = "com.squareup.moshi:moshi:${Versions.moshi}" // It makes it easy to parse JSON into Kotlin objects
    const val moshiCodegen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}" // Add codegen to moshi (generating by using @JsonClass(generateAdapter = true))
    const val moshiKotlin = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}" // Add reflection to moshi (better not to use : 2.5 MB) Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    const val moshiAdapter = "com.squareup.moshi:moshi-adapters:${Versions.moshi}"

    // --- Dagger ---
    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerAnnotation = "com.google.dagger:dagger-compiler:${Versions.dagger}"

    object Test {
        const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.2"
        const val testCore = "androidx.test:core:1.4.0" // Core library
        const val testCoreKtx = "androidx.test:core-ktx:1.4.0"
        const val testRunner = "androidx.test:runner:1.4.0" // AndroidJUnitRunner
        const val testRules = "androidx.test:rules:1.2.0" // JUnit Rules
        const val testJunit = "androidx.test.ext:junit:1.1.3" // Assertions and JUnit 4 framework
        const val testJunitKtx = "androidx.test.ext:junit-ktx:1.1.3" // Assertions
        const val testTruth2 = "com.google.truth:truth:1.1.3"
        const val testTruth = "androidx.test.ext:truth:1.4.0"
        const val coreTesting = "androidx.arch.core:core-testing:2.1.0"
        const val mockitoWeb = "com.squareup.okhttp3:mockwebserver:4.4.0"
        const val mockito = "org.mockito:mockito-core:3.2.4"
        const val mockitokotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0" // A small library that provides helper functions to work with Mockito in Kotlin.

        const val testWorking = "androidx.work:work-testing:${Versions.workManager}"

        const val espresso = "androidx.test.espresso:espresso-core:3.2.0"
        const val espressoIntents = "androidx.test.espresso:espresso-intents:3.2.0"
    }
}

private fun DependencyHandler.api(dependencyNotation: Any) = add("api", dependencyNotation)
private fun DependencyHandler.implementation(dependencyNotation: Any) = add("implementation", dependencyNotation)
private fun DependencyHandler.kapt(dependencyNotation: Any) = add("kapt", dependencyNotation)
private fun DependencyHandler.androidTestImplementation(dependencyNotation: Any) = add("androidTestImplementation", dependencyNotation)
private fun DependencyHandler.testImplementation(dependencyNotation: Any) = add("testImplementation", dependencyNotation)

fun DependencyHandler.addTestDependencies() {
    testImplementation(Lib.kotlinReflection)
    testImplementation(Lib.Test.testCore)
    testImplementation(Lib.Test.testCoreKtx)
    testImplementation(Lib.Test.testRunner)
    testImplementation(Lib.Test.testRules)
    testImplementation(Lib.Test.testJunit)
    testImplementation(Lib.Test.testJunitKtx)
    testImplementation(Lib.Test.testTruth)
    testImplementation(Lib.Test.testTruth2)
    testImplementation(Lib.Test.coreTesting)
    testImplementation(Lib.Test.mockitoWeb)
    testImplementation(Lib.Test.mockito)
    testImplementation(Lib.Test.mockitokotlin)
    testImplementation(Lib.Test.coroutinesTest)
}

fun DependencyHandler.addBaseDependencies() {
    api(Lib.fragmentKtx)
    api(Lib.activityKtx)
    api(Lib.constraintLayout)
    api(Lib.material)
    api(Lib.coil)
    api(Lib.coreKtx)
    api(Lib.springAnimation)
    api(Lib.springAnimationKtx)
    implementation(Lib.exoPlayerCore)
    implementation(Lib.exoPlayerUi)
    implementation(Lib.exoPlayerMediaSession)
    implementation(Lib.exoPlayerHls)
    api(Lib.recyclerView)
    api(Lib.dataStorePref)

    api(Lib.coroutinesLifecycle)
    api(Lib.coroutinesLivedata)
    api(Lib.coroutinesViewmodel)
    api(Lib.coroutines)

    implementation(Lib.playCore)
    implementation(Lib.playCoreKtx)

    implementation(Lib.room)
    implementation(Lib.roomKtx)
    kapt(Lib.roomAnnotation)

    implementation(Lib.retrofit)
    implementation(Lib.retrofitMoshiConverter)
    implementation(Lib.retrofitInterceptor)
    implementation(Lib.retrofitScalars)
    implementation(Lib.moshi)
    kapt(Lib.moshiCodegen)

    implementation(Lib.workManager)
    implementation(Lib.workManagerKotlin)

    implementation(Lib.dagger)
    kapt(Lib.daggerAnnotation)

    implementation(Lib.preference)
    implementation(Lib.preferenceKtx)
}