plugins {
    id("com.android.library")
    id("convention.android-base")
    id("convention.kotlin-base")
    kotlin("android")
}

android {
    androidComponents {
        beforeVariants { variantBuilder ->
            if (variantBuilder.buildType == "debug") variantBuilder.enable = false
        }
    }
}