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

// Performance benchmarking tasks
tasks.register("benchmarkModulePerformance") {
    group = "profiling"
    description = "Run performance benchmark comparing full vs selective module builds"
    
    doLast {
        val profilerBin = if (System.getProperty("os.name").lowercase().contains("windows")) {
            "gradle/profiler/bin/gradle-profiler.bat"
        } else {
            "gradle/profiler/bin/gradle-profiler"
        }
        
        println("🚀 Starting performance benchmark...")
        println("📊 Comparing full project vs selective module builds")
        
        exec {
            commandLine(
                profilerBin,
                "--benchmark",
                "--output-dir", "gradle/profiler/last-output",
                "--scenario-file", "gradle/profiler/profiler.scenarios",
                "build_full_project",
                "build_selective_app", 
                "build_selective_demo"
            )
        }
        
        println("✅ Benchmark completed! Check gradle/profiler/last-output/ for results")
        println("📈 HTML Report: gradle/profiler/last-output/benchmark.html")
        println("📋 CSV Data: gradle/profiler/last-output/benchmark.csv")
    }
}

tasks.register("benchmarkConfigurationTime") {
    group = "profiling"
    description = "Benchmark Gradle configuration time for full vs selective builds"
    
    doLast {
        val profilerBin = if (System.getProperty("os.name").lowercase().contains("windows")) {
            "gradle/profiler/bin/gradle-profiler.bat"  
        } else {
            "gradle/profiler/bin/gradle-profiler"
        }
        
        println("⚙️ Benchmarking configuration time...")
        
        exec {
            commandLine(
                profilerBin,
                "--benchmark",
                "--output-dir", "gradle/profiler/last-output", 
                "--scenario-file", "gradle/profiler/profiler.scenarios",
                "config_full_vs_selective",
                "config_selective"
            )
        }
        
        println("✅ Configuration benchmark completed!")
    }
}
