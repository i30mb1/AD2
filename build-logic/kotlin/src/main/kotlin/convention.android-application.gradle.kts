import com.android.build.gradle.AppExtension

plugins {
    id("com.android.application")
    id("convention.android-base")
    id("convention.kotlin-base")
    kotlin("android")
}

configure<AppExtension> {
    buildTypes {
        getByName("debug") {
            setMatchingFallbacks("release")
        }
    }
}