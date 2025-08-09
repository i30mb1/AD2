plugins {
    id("org.jetbrains.kotlinx.kover")
}

// Kover 0.8.3+ configuration
kover {
    reports {
        // Configure total project coverage reports
        total {
            html {
                onCheck = false
            }

            xml {
                onCheck = false
            }

            verify {
                onCheck = true
                rule {
                    minBound(30) // Lowered threshold for existing code
                }
            }
        }

        // Configure filters for all report types  
        filters {
            excludes {
                classes(
                    "*Fragment",
                    "*Fragment\$*",
                    "*Activity",
                    "*Activity\$*",
                    "*.databinding.*",
                    "*.BuildConfig"
                )
            }
        }
    }
}
