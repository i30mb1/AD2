plugins {
    id("convention.android-library")
    id("convention.compose")
    id("n7.plugins.kotlin-kapt")
    `maven-publish`
}

android {
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

    publishing {
        publications {
            create<MavenPublication>("maven") {
                group = project.group
                artifactId = project.name
                version = "1.0"

                afterEvaluate {
                    from(components["release"])
                }
            }
        }
    }

dependencies {
    implementation(libs.bundles.camera)
    implementation(libs.bundles.kotlinDL)

    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.navigator)

    implementation(projects.feature.camera.domain.wiring)
    implementation(projects.feature.camera.domain.api)

    testImplementation(libs.test.truth)
    testImplementation(libs.test.coroutines)
    testImplementation(libs.test.coroutines.debug)
    testImplementation(libs.test.lifecycle)

    androidTestImplementation(libs.test.lifecycle)
    androidTestImplementation(libs.test.coroutines)
    androidTestImplementation(libs.test.junit)
    androidTestImplementation(libs.test.junit.kotlin)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(projects.feature.camera.domain.impl)
    androidTestImplementation(libs.test.core)
    androidTestImplementation(libs.test.rules)

    kapt(libs.daggerAnnotation)
}
