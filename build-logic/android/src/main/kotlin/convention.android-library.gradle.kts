plugins {
    id("com.android.library")
    id("convention.android-base")
    id("convention.kotlin-base")
}

android {
    namespace = "$applicationID.${project.name.replace("-", "")}"
    androidComponents {
        beforeVariants { variantBuilder ->
            if (variantBuilder.buildType == "debug") variantBuilder.enable = false
        }
    }
}