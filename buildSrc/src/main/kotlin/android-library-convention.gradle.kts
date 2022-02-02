import com.android.build.gradle.LibraryExtension

plugins {
    id("com.android.library")
    id("base-android-convention")
    id("kotlin-android")
    id("base-kotlin-convention")
}

configure<LibraryExtension> {
    variantFilter { if (name == "debug") ignore = true }
}