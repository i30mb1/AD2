import org.gradle.api.artifacts.dsl.DependencyHandler

object Lib {
    const val appCompat = "androidx.appcompat:appcompat:1.4.0"
    const val dataStorePref = "androidx.datastore:datastore-preferences:1.0.0"
    const val recyclerView = "androidx.recyclerview:recyclerview:1.2.0-beta02"
    const val kotlinReflection = "org.jetbrains.kotlin:kotlin-reflect:1.5.30"
    const val ticker = "com.robinhood.ticker:ticker:1.2.2"
    const val retrofit = "com.squareup.retrofit2:retrofit:2.9.0"
    const val retrofitMoshiConverter = "com.squareup.retrofit2:converter-moshi:2.9.0"
    const val retrofitInterceptor = "com.squareup.okhttp3:logging-interceptor:4.9.3"
    const val retrofitScalars = "com.squareup.retrofit2:converter-scalars:2.9.0"
    const val paging3 = "androidx.paging:paging-runtime:3.0.0"
    const val material = "com.google.android.material:material:1.2.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"
    const val coil = "io.coil-kt:coil:0.13.0"
    const val jsoup = "org.jsoup:jsoup:1.11.2" // Jsoup для разбора html
    const val firebaseAnal = "com.google.firebase:firebase-analytics:17.3.0"
    const val playCore = "com.google.android.play:core:1.10.3"
    const val playCoreKtx = "com.google.android.play:core-ktx:1.8.1" // auto update + rateMe
    const val coreKtx = "androidx.core:core-ktx:1.7.0" // Write more concise, idiomatic Kotlin code.
    const val jsonSimple = "com.googlecode.json-simple:json-simple:1.1.1"
    const val springAnimation = "androidx.dynamicanimation:dynamicanimation:1.0.0"
    const val springAnimationKtx = "androidx.dynamicanimation:dynamicanimation-ktx:1.0.0-alpha03"
    const val instantApps = "com.google.android.gms:play-services-instantapps:17.0.0"
    const val viewPager2 = "androidx.viewpager2:viewpager2:1.0.0"
    const val splashScreen = "androidx.core:core-splashscreen:1.0.0-alpha02"

    const val exoPlayerCore = "com.google.android.exoplayer:exoplayer:2.16.1"

    private const val workManagerVersion = "2.7.1"
    const val workManager = "androidx.work:work-runtime:2.0.1"
    const val workManagerKotlin = "androidx.work:work-runtime-ktx:$workManagerVersion"
    const val workGCM = "androidx.work:work-gcm:$workManagerVersion"

    const val preference = "androidx.preference:preference:1.1.1"
    const val preferenceKtx = "androidx.preference:preference-ktx:1.1.1"

    private const val roomVersion = "2.3.0"
    const val room = "androidx.room:room-runtime:$roomVersion"
    const val roomAnnotation = "androidx.room:room-compiler:$roomVersion"
    const val roomKtx = "androidx.room:room-ktx:$roomVersion" // kotlin Extensions and Coroutines support for Room

    private const val coroutinesVersion = "1.6.0"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion" // for testing coroutines

    const val lifecycleVersion = "2.4.0"
    const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion" // liveData (LiveData + coroutines)
    const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion" // viewModelScope + savedStateHandle
    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion" // flowWithLifecycle
    const val fragmentKtx = "androidx.fragment:fragment-ktx:1.4.0" // easy fragment transaction + by viewModels()
    const val activityKtx = "androidx.activity:activity-ktx:1.1.0" // on BackPress support for Fragment

    private const val moshiVersion = "1.12.0"
    const val moshi = "com.squareup.moshi:moshi:$moshiVersion" // It makes it easy to parse JSON into Kotlin objects
    const val moshiCodegen = "com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion" // Add codegen to moshi (generating by using @JsonClass(generateAdapter = true))
    const val moshiKotlin = "com.squareup.moshi:moshi-kotlin:$moshiVersion" // Add reflection to moshi (better not to use : 2.5 MB) Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    const val moshiAdapter = "com.squareup.moshi:moshi-adapters:$moshiVersion"

    private const val daggerVersion = "2.40.2"
    const val dagger = "com.google.dagger:dagger:$daggerVersion"
    const val daggerAnnotation = "com.google.dagger:dagger-compiler:$daggerVersion"

    private const val lintVersion = "30.0.4" // gradlePlugin + 23
    const val lintApi = "com.android.tools.lint:lint-api:$lintVersion"
    const val lintChecks = "com.android.tools.lint:lint-checks:$lintVersion"

    object Test {
        const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0-RC"
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

        const val espresso = "androidx.test.espresso:espresso-core:3.2.0"
        const val espressoIntents = "androidx.test.espresso:espresso-intents:3.2.0"
    }
}

private fun DependencyHandler.api(dependencyNotation: Any) = add("api", dependencyNotation)
fun DependencyHandler.lintChecks(dependencyNotation: Any) = add("lintChecks", dependencyNotation)
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
    api(Lib.springAnimation)
    api(Lib.springAnimationKtx)
    api(Lib.dataStorePref)
    kapt(Lib.moshiCodegen)
    api(Lib.moshi)

    implementation(Lib.room)
    implementation(Lib.roomKtx)
    kapt(Lib.roomAnnotation)

    implementation(Lib.workManager)
    implementation(Lib.workManagerKotlin)

    implementation(Lib.preference)
    implementation(Lib.preferenceKtx)
}