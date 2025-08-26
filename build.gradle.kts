plugins {
    id("bump-version-plugin")
    id("measure-build-plugin")
    id("convention.detekt")
    id("com.osacky.doctor") version "0.12.0" apply false
//    id("com.autonomousapps.dependency-analysis") version "1.0.0-rc02"
    // https://arrow-kt.io/docs/meta/analysis/
}


//if (!isCI()) {
//    apply(plugin = "com.osacky.doctor")
//}

bumpVersionConfig {
    isEnabled = false
}

branchComparison {
    baseBranch.set("v1")
    iterations.set(3)
    warmupRuns.set(1)
    targetTasks.set(listOf(":app:help"))
    outputDir.set("gradle/profiler/last-output")
}
