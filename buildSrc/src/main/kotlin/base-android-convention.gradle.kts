import com.android.build.gradle.BaseExtension

configure<BaseExtension> {
    compileSdkVersion(Apps.compileSdk)

    buildFeatures.viewBinding = true

    defaultConfig {
        minSdk = Apps.minSdkVersion
        targetSdk = Apps.targetSdkVersion
    }
}
