import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BuildType
import org.gradle.configurationcache.extensions.capitalized

plugins {
    jacoco
}

jacoco {
}

tasks.withType<Test>().configureEach {
    maxParallelForks = 3
    configure<JacocoTaskExtension> {
        isEnabled = true
//        includes = emptyList()
//        excludes = emptyList()
//        isIncludeNoLocationClasses = true
    }
}

project.afterEvaluate {
    val projectName = project.name
    println("NAME ----> $projectName")
    if (isAndroidModule(project)) setupAndroidReporting()
    else setupKotlinReporting()
}

fun isAndroidModule(project: Project): Boolean {
    val isAndroidLibrary = project.plugins.hasPlugin("com.android.library")
    val isAndroidApp = project.plugins.hasPlugin("com.android.application")
    return isAndroidApp || isAndroidLibrary
}

fun setupAndroidReporting() {

//    afterEvaluate {
//        android {
//
//        }
//    }
    configure<BaseExtension> {
        val productFlavors = productFlavors.map { flavor -> flavor.name }.toMutableList()
        val buildType = buildTypes.filter { type: BuildType -> true }.map { type -> type.name }
        println("buildType -----> $buildType")
        println("productFlavors -----> $productFlavors")
        if (productFlavors.isEmpty()) productFlavors.add("")
        productFlavors.forEach { productFlavorsName ->
            buildType.forEach { buildTypeName ->
                val name = when (productFlavorsName.isEmpty()) {
                    false -> "$productFlavorsName${buildTypeName.capitalized()}"
                    true -> buildTypeName
                }
                val path = "$productFlavorsName/$buildTypeName"
                val jacocoTaskName = "jacoco${name.capitalized()}Report"
                val testTaskName = "test${name.capitalized()}UnitTest"
                println("testTaskName ------> $testTaskName")
                tasks.register<JacocoReport>(jacocoTaskName) {
                    dependsOn(testTaskName)
                    group = "verification"
                    val dirs = listOf("src/main/kotlin")
                    val kotlinTree = fileTree("$buildDir/tmp/kotlin-classes/$name") {
                        include()
                    }
                    classDirectories.setFrom(kotlinTree)
                    executionData.setFrom("$buildDir/jacoco/$testTaskName.exec")
                    sourceDirectories.setFrom(dirs)
                    additionalSourceDirs.setFrom(dirs)

                    reports {
                        html.required.set(true)
                        html.outputLocation.set(file("${buildDir}/reports/jacoco"))
                    }
                }
            }
        }
    }
}

fun setupKotlinReporting() {
    tasks.withType<JacocoReport>().configureEach {
        dependsOn(tasks.getByName("test"))
        reports {
            html.required.set(true)
            html.outputLocation.set(file("${buildDir}/reports/jacoco"))
        }
        sourceDirectories.setFrom(sourceDirectoriesTree)
        classDirectories.setFrom(classDirectoriesTree)
        executionData.setFrom(executionDataTree)

    }
    tasks.withType<JacocoCoverageVerification>().configureEach {
//        sourceDirectories.setFrom()
    }
}

val fileFilter = listOf<String>(
//    "**/*"
)

val executionDataTree = fileTree(project.buildDir) {
    include("jacoco/test.exec")
}

val classDirectoriesTree = fileTree(project.buildDir) {
    exclude(fileFilter)
}

val sourceDirectoriesTree = fileTree(project.buildDir) {
    exclude("src/main/kotlin/**/*")
}