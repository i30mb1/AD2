plugins {
    id("bump-version-plugin")
    id("measure-build-plugin")
    id("convention.detekt")
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

branchComparison {
    baseBranch.set("master")           // базовая ветка для сравнения
    iterations.set(6)                  // количество итераций
    targetTasks.set(listOf(":app:assembleDebug")) // задачи для бенчмарка
    outputDir.set("gradle/profiler/last-output")  // папка с результатами
}
