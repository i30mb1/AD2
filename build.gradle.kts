plugins {
    id("file-line-prefix-plugin")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}