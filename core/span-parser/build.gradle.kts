plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
    id("convention.kover")
}

android {
//    kotlin {
//        explicitApi()
//    }
}

dependencies {
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.android))

    testImplementation(libs.testTruth)
    testImplementation(libs.mockitokotlin)
    testImplementation(testFixtures(project(Module.Core.common)))

    kapt(libs.daggerAnnotation)
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
