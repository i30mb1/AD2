plugins {
    id("com.android.library")
    id("convention.android-base")
    id("convention.kotlin-base")
}

android {
    namespace = "$applicationID.${project.path.replace("-", "").replace(":", ".").drop(1)}"
    androidComponents {
        beforeVariants { variantBuilder ->
            if (variantBuilder.buildType == "debug" && project.name != "ui") variantBuilder.enable = false
        }
    }
}
