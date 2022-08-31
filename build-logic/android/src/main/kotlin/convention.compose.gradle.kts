@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.BaseExtension

//private val catalogs = extensions.getByType<VersionCatalogsExtension>()
//private val composeVersion = catalogs.named("libs").findVersion("compose").get().requiredVersion
//private val composeBundle = catalogs.named("libs").findBundle("compose").get()

configure<BaseExtension> {
    buildFeatures.compose = true
    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }
}

dependencies {
//    implementation(composeBundle)
}