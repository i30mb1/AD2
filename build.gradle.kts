plugins {
    id("bump-version-plugin")
    id("measure-build-plugin")
//    id("convention.detekt")
    id("com.osacky.doctor") version "0.9.1" apply false
//    id("com.autonomousapps.dependency-analysis") version "1.0.0-rc02"
    // https://arrow-kt.io/docs/meta/analysis/
}


//if (!isCI()) {
//    apply(plugin = "com.osacky.doctor")
//}

bumpVersionConfig {
    isEnabled = false
}
