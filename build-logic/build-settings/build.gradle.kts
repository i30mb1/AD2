plugins {
    `kotlin-dsl`
    `maven-publish`
}

gradlePlugin {
    plugins {
        register("SettingsPlugin") {
            id = "n7.plugins.settings"
            implementationClass = "n7.plugins.SettingsPlugin"
            displayName = "Settings Plugin"
        }
        register("IncludeModulesPlugin") {
            id = "n7.plugins.include-modules"
            implementationClass = "n7.plugins.IncludeModulesPlugin"
            displayName = "Include Modules Plugin"
        }
        register("GitHooksPlugin") {
            id = "n7.plugins.git-hooks"
            implementationClass = "n7.plugins.GitHooksPlugin"
            displayName = "Git Hooks Plugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            group = "n7"
            artifactId = "convention"
            version = "1.0"
            from(components["java"])
        }
    }
}