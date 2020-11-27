import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler

object Apps {
    const val compileSdk    = 30
    const val buildToolsSdk = "30.0.2"
    const val applicationId = "n7.ad2"
    const val minSdk        = 23
    const val targetSdk     = 30
    const val versionCode   = 555
    const val versionName   = "555"
}

object Versions {
    const val gradlePlugin  = "4.1.0"
    const val lint          = "27.1.0" // gradlePlugin + 23
    const val workManager   = "2.3.4"
    const val moshi         = "1.9.2"
    const val room          = "2.2.5"
    const val kotlin        = "1.4.10"
}

object Lib {
    const val kotlinReflection = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    const val ticker           = "com.robinhood.ticker:ticker:1.2.2"
    const val sliding          = "com.yarolegovich:sliding-root-nav:1.1.0"
    const val debugDB          = "com.amitshekhar.android:debug-db:1.0.1" // cmd ipconfig основной шлюз + :8080 (как узнать страничку для браузера)
    const val retrofit         = "com.squareup.retrofit2:retrofit:2.7.1"
    const val gsonConverter    = "com.squareup.retrofit2:converter-gson:2.6.2"
    const val paging           = "androidx.paging:paging-runtime-ktx:2.1.2"
    const val paging3          = "androidx.paging:paging-runtime:3.0.0-alpha09"
    const val material         = "com.google.android.material:material:1.2.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0"
    const val coil             = "io.coil-kt:coil:0.13.0"
    const val picasso          = "com.squareup.picasso:picasso:2.71828"
    const val picassoTrans     = "jp.wasabeef:picasso-transformations:2.2.1"
    const val picassoTrans2    = "jp.co.cyberagent.android.gpuimage:gpuimage-library:1.4.1"
    const val jsoup            = "org.jsoup:jsoup:1.11.2" // Jsoup для разбора html
    const val firebaseAnal     = "com.google.firebase:firebase-analytics:17.3.0"
    const val playCore         = "com.google.android.play:core:1.8.0" // auto update + rateMe
    const val playCoreKtx      = "com.google.android.play:core-ktx:1.8.1"
    const val exoPlayer        = "com.google.android.exoplayer:exoplayer-core:2.11.4"
    const val coreKtx          = "androidx.core:core-ktx:1.2.0" // Write more concise, idiomatic Kotlin code.
    const val jsonSimple       = "com.googlecode.json-simple:json-simple:1.1.1"
    const val kotlinStdlib     = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    const val springAnimation     = "androidx.dynamicanimation:dynamicanimation:1.0.0"
    const val springAnimationKtx  = "androidx.dynamicanimation:dynamicanimation-ktx:1.0.0-alpha03"
    const val instantApps  = "com.google.android.gms:play-services-instantapps:17.0.0"
    const val viewPager2  = "androidx.viewpager2:viewpager2:1.0.0"

    const val workManager      = "androidx.work:work-runtime:2.0.1"
    const val workManagerKotlin      = "androidx.work:work-runtime-ktx:${Versions.workManager}"
    const val workGCM = "androidx.work:work-gcm:${Versions.workManager}"


    // --- Preference ---
    const val preference       = "androidx.preference:preference:1.1.1"
    const val preferenceKtx    = "androidx.preference:preference-ktx:1.1.1"

    // --- Room ---
    const val room           = "androidx.room:room-runtime:${Versions.room}"
    const val roomAnnotation = "androidx.room:room-compiler:${Versions.room}"
    const val roomKtx        = "androidx.room:room-ktx:${Versions.room}" // kotlin Extensions and Coroutines support for Room

    // --- Coroutines ---
    const val coroutines          = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0"
    const val coroutinesAndroid   = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.0" // for testing coroutines
    const val coroutinesLifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:2.2.0" // lifecycleScope + launchWhenResumed and ets.
    const val coroutinesLivedata  = "androidx.lifecycle:lifecycle-livedata-ktx:2.2.0" // liveData (LiveData + coroutines)
    const val coroutinesViewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0" // viewModelScope + savedStateHandle
    const val lifecycleAnnotation = "androidx.lifecycle:lifecycle-common-java8:2.2.0" // that's only needed if you have lifecycle-related annotations in your code, specifically @OnLifecycleEvent
    const val fragmentKtx         = "androidx.fragment:fragment-ktx:1.3.0-alpha06" // easy fragment transaction + by viewModels()
    const val activityKtx         = "androidx.activity:activity-ktx:1.1.0" // on BackPress support for Fragment

    // --- Moshi ---
    const val moshi        = "com.squareup.moshi:moshi:${Versions.moshi}" // It makes it easy to parse JSON into Kotlin objects
    const val moshiCodegen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}" // Add codegen to moshi (generating by using @JsonClass(generateAdapter = true))
    const val moshiKotlin  = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}" // Add reflection to moshi (better not to use : 2.5 MB) Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    const val moshiAdapter = "com.squareup.moshi:moshi-adapters:${Versions.moshi}"

    // --- Dagger ---
    const val dagger                   = "com.google.dagger:dagger:2.25.2"
    const val daggerAnnotation         = "com.google.dagger:dagger-compiler:2.25.2"
    const val daggerAssisted           = "com.squareup.inject:assisted-inject-annotations-dagger2:0.5.2"
    const val daggerAssistedAnnotation = "com.squareup.inject:assisted-inject-processor-dagger2:0.5.2"

    object Test {
        const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.2"
        const val testCore       = "androidx.test:core:1.2.0" // Core library
        const val testCoreKtx    = "androidx.test:core-ktx:1.2.0"
        const val testRunner     = "androidx.test:runner:1.2.0" // AndroidJUnitRunner
        const val testRules      = "androidx.test:rules:1.2.0" // JUnit Rules
        const val testJunit      = "androidx.test.ext:junit:1.1.1" // Assertions and JUnit 4 framework
        const val testJunitKtx   = "androidx.test.ext:junit-ktx:1.1.1" // Assertions
        const val testTruth2     = "com.google.truth:truth:0.44"
        const val testTruth      = "androidx.test.ext:truth:1.2.0"
        const val coreTesting    = "androidx.arch.core:core-testing:2.1.0"
        const val mockitoWeb     = "com.squareup.okhttp3:mockwebserver:4.4.0"
        const val mockito        = "org.mockito:mockito-core:3.2.4"
        const val mockitokotlin  = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0" // A small library that provides helper functions to work with Mockito in Kotlin.

        const val testWorking    = "androidx.work:work-testing:${Versions.workManager}"

        const val espresso        = "androidx.test.espresso:espresso-core:3.2.0"
        const val espressoIntents = "androidx.test.espresso:espresso-intents:3.2.0"
    }
}

private fun DependencyHandler.androidTestImplementation(dependencyNotation: Any): Dependency? =
        add("androidTestImplementation", dependencyNotation)

private fun DependencyHandler.testImplementation(dependencyNotation: Any): Dependency? =
        add("testImplementation", dependencyNotation)

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