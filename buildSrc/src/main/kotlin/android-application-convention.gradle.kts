import com.android.build.gradle.AppExtension

plugins {
    id("com.android.application")
    id("base-android-convention")
    id("kotlin-android")
    id("base-kotlin-convention")
}

configure<AppExtension> {
    buildTypes {
        getByName("debug") {
            setMatchingFallbacks("release")
        }
    }
}