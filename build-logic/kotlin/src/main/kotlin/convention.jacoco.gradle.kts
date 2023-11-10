import com.android.build.gradle.BaseExtension
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
    }
}

project.afterEvaluate {
    if (isAndroidModule(project)) setupAndroidReporting()
    else setupKotlinReporting()
}

fun isAndroidModule(project: Project): Boolean {
    val isAndroidLibrary = project.plugins.hasPlugin("com.android.library")
    val isAndroidApp = project.plugins.hasPlugin("com.android.application")
    return isAndroidApp || isAndroidLibrary
}

fun setupAndroidReporting() {
    configure<BaseExtension> {
        val productFlavors = productFlavors.map { flavor -> flavor.name }.toMutableList()
        val buildType = buildTypes.map { type -> type.name }
        if (productFlavors.isEmpty()) productFlavors.add("")
        productFlavors.forEach { productFlavorsName ->
            buildType.forEach { buildTypeName ->
                val name = when (productFlavorsName.isEmpty()) {
                    false -> "$productFlavorsName${buildTypeName.capitalized()}"
                    true -> buildTypeName
                }
                val jacocoTaskName = "jacoco${name.capitalized()}Report"
                val testTaskName = "test${name.capitalized()}UnitTest"
                tasks.register<JacocoReport>(jacocoTaskName) {
                    dependsOn(testTaskName)
                    group = "verification"
                    val dirs = listOf("src/main/kotlin")
                    val kotlinTree = fileTree("${layout.buildDirectory}/tmp/kotlin-classes/$name")
                    classDirectories.setFrom(kotlinTree)
                    executionData.setFrom("${layout.buildDirectory}/jacoco/$testTaskName.exec")
                    sourceDirectories.setFrom(dirs)
                    additionalSourceDirs.setFrom(dirs)
                    reports {
                        html.required.set(true)
                        html.outputLocation.set(file("${layout.buildDirectory}/reports/jacoco"))
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
            html.outputLocation.set(file("${layout.buildDirectory}/reports/jacoco"))
        }
        sourceDirectories.setFrom(fileTree(project.layout.buildDirectory) {
            exclude("src/main/kotlin/**/*")
        })
        classDirectories.setFrom(fileTree(project.layout.buildDirectory) {
//            exclude(listOf("**/*"))
        })
        executionData.setFrom(fileTree(project.layout.buildDirectory) {
            include("jacoco/test.exec")
        })

    }
    tasks.withType<JacocoCoverageVerification>().configureEach {
//        sourceDirectories.setFrom()
    }
}
