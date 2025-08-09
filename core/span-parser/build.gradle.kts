plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-ksp")
    id("convention.kover")
}

android {
//    kotlin {
//        explicitApi()
//    }
}

dependencies {
    implementation(projects.core.dagger)
    implementation(projects.core.commonAndroid)

    testImplementation(libs.test.truth)
    testImplementation(testFixtures(projects.core.commonJvm))

    ksp(libs.daggerAnnotation)
}

koverReport {
    // filters for all report types of all build variants
    filters {
        excludes {
            classes(
                "*Fragment",
                "*Fragment\$*",
                "*Activity",
                "*Activity\$*",
                "*.databinding.*",
                "*.BuildConfig"
            )
        }
    }

    androidReports("release") {
        // filters for all report types only of 'release' build type
        filters {
            excludes {
                classes(
                    "*Fragment",
                    "*Fragment\$*",
                    "*Activity",
                    "*Activity\$*",
                    "*.databinding.*",
                    "*.BuildConfig",

                    // excludes debug classes
                    "*.DebugUtil"
                )
            }
        }
    }

}
