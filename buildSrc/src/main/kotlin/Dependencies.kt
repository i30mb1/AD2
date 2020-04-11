object Apps {
    const val compileSdk    = 28
    const val buildToolsSdk = "29.0.2"
    const val applicationId = "n7.ad2"
    const val minSdk        = 23
    const val targetSdk     = 28
    const val versionCode   = 1
    const val versionName   = "1"
}

object Lib {
    const val ticker           = "com.robinhood.ticker:ticker:1.2.2"
    const val sliding          = "com.yarolegovich:sliding-root-nav:1.1.0"
    const val debugDB          = "com.amitshekhar.android:debug-db:1.0.1" // cmd ipconfig основной шлюз + :8080 (как узнать страничку для браузера)
    const val retrofit         = "com.squareup.retrofit2:retrofit:2.7.1"
    const val gsonConverter    = "com.squareup.retrofit2:converter-gson:2.6.2"
    const val workManager      = "androidx.work:work-runtime:2.0.1"
    const val paging           = "androidx.paging:paging-runtime:2.0.0"
    const val material         = "com.google.android.material:material:1.2.0-alpha05"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
    const val coil             = "io.coil-kt:coil:0.9.5"
    const val picasso          = "com.squareup.picasso:picasso:2.71828"
    const val picassoTrans     = "jp.wasabeef:picasso-transformations:2.2.1"
    const val picassoTrans2    = "jp.co.cyberagent.android.gpuimage:gpuimage-library:1.4.1"
    const val jsoup            = "org.jsoup:jsoup:1.11.2" // Jsoup для разбора html
    const val firebaseAnal     = "com.google.firebase:firebase-analytics:17.3.0"
    const val playCore         = "com.google.android.play:core:1.7.2" // auto update
    const val exoPlayer        = "com.google.android.exoplayer:exoplayer-core:2.10.2"
    const val coreKtx          = "androidx.core:core-ktx:1.2.0" // Write more concise, idiomatic Kotlin code.
    const val jsonSimple       = "com.googlecode.json-simple:json-simple:1.1.1"
    const val kotlinStdlib     = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.71"

    // --- Preference ---
    const val preference       = "androidx.preference:preference:1.1.0"
    const val preferenceKtx    = "androidx.preference:preference-ktx:1.1.0"

    // --- Room ---
    const val room           = "androidx.room:room-runtime:2.2.4"
    const val roomAnnotation = "androidx.room:room-compiler:2.2.4"
    const val roomKtx        = "androidx.room:room-ktx:2.2.4" // kotlin Extensions and Coroutines support for Room

    // --- Coroutines ---
    const val coroutines          = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2"
    const val coroutinesAndroid   = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.2" // for testing coroutines
    const val coroutinesLifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:2.2.0" // lifecycleScope + launchWhenResumed and ets.
    const val coroutinesLivedata  = "androidx.lifecycle:lifecycle-livedata-ktx:2.2.0" // liveData (LiveData + coroutines)
    const val coroutinesViewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0" // viewModelScope + savedStateHandle
    const val lifecycleAnnotation = "androidx.lifecycle:lifecycle-common-java8:2.2.0" // that's only needed if you have lifecycle-related annotations in your code, specifically @OnLifecycleEvent
    const val fragmentKtx         = "androidx.fragment:fragment-ktx:1.2.3" // easy fragment transaction + by viewModels()
    const val activityKtx         = "androidx.activity:activity-ktx:1.1.0" // on BackPress support for Fragment

    // --- Moshi ---
    const val moshi        = "com.squareup.moshi:moshi:1.9.2" // It makes it easy to parse JSON into Kotlin objects
    const val moshiCodegen = "com.squareup.moshi:moshi-kotlin-codegen:1.9.2" // Add codegen to moshi (generating by using @JsonClass(generateAdapter = true))
    const val moshiKotlin  = "com.squareup.moshi:moshi-kotlin:1.9.2" // Add reflection to moshi (better not to use : 2.5 MB)
    const val moshiAdapter = "com.squareup.moshi:moshi-adapters:1.9.2"

    // --- Dagger ---
    const val dagger                   = "com.google.dagger:dagger:2.25.2"
    const val daggerAnnotation         = "com.google.dagger:dagger-compiler:2.25.2"
    const val daggerAssisted           = "com.squareup.inject:assisted-inject-annotations-dagger2:0.5.2"
    const val daggerAssistedAnnotation = "com.squareup.inject:assisted-inject-processor-dagger2:0.5.2"
}