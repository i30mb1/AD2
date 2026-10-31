plugins {
    id("convention.kotlin-jvm")
    application
}

application {
    mainClass.set("n7.ad2.parser.heroes.HeroParserKt")
}

// ./gradlew :core:parser:runHeroes -PtestMode=false       → parse heroes (Valve datafeed + wiki trivia)
// ./gradlew :core:parser:runHeroes -Pheroes=Pudge,Lion    → parse only these heroes
// ./gradlew :core:parser:runHeroes -PparseResponses=true  → also re-download voice responses from the wiki
// ./gradlew :core:parser:runItems                         → parse items (Valve datafeed + game files + RU/EN wiki)
// ./gradlew :core:parser:runItems -Pitems=blink_dagger    → parse only these item folders
// ./gradlew :core:parser:runItems -Pwiki=false            → skip the wiki, keep tips/trivia of the current files
// ./gradlew :core:parser:runItems -Picons=refresh         → re-download every item icon from the Valve CDN
// ./gradlew :core:parser:runItems -PdryRun=true           → only print which items are new / removed from the game
// Default test mode parses the first 3 heroes only

tasks.register<JavaExec>("runHeroes") {
    group = "parser"
    description = "Parse Dota 2 heroes from the wiki and generate JSON data files"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("n7.ad2.parser.heroes.HeroParserKt")
    workingDir = rootProject.projectDir  // assetsDatabase uses user.dir — must point to repo root
    val cfClearance = findProperty("cfClearance")?.toString() ?: System.getenv("CF_CLEARANCE") ?: ""
    val htmlDir = findProperty("htmlDir")?.toString() ?: System.getenv("PARSER_HTML_DIR") ?: ""
    val testMode = findProperty("testMode")?.toString() ?: System.getenv("TEST_MODE") ?: "true"
    val testLimit = findProperty("testLimit")?.toString() ?: System.getenv("TEST_LIMIT") ?: "3"
    val heroes = findProperty("heroes")?.toString() ?: System.getenv("HEROES") ?: ""
    val parseResponses = findProperty("parseResponses")?.toString() ?: System.getenv("PARSE_RESPONSES") ?: "false"
    if (cfClearance.isNotEmpty()) environment("CF_CLEARANCE", cfClearance)
    if (htmlDir.isNotEmpty()) environment("PARSER_HTML_DIR", htmlDir)
    if (heroes.isNotEmpty()) environment("HEROES", heroes)
    environment("TEST_MODE", testMode)
    environment("TEST_LIMIT", testLimit)
    environment("PARSE_RESPONSES", parseResponses)
}

tasks.register<JavaExec>("runItems") {
    group = "parser"
    description = "Parse Dota 2 items from the Valve datafeed and generate JSON data files"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("n7.ad2.parser.items.ValveItemParserKt")
    workingDir = rootProject.projectDir  // assetsDatabase uses user.dir — must point to repo root
    val items = findProperty("items")?.toString() ?: System.getenv("ITEMS") ?: ""
    val wiki = findProperty("wiki")?.toString() ?: System.getenv("ITEMS_WIKI") ?: "true"
    val icons = findProperty("icons")?.toString() ?: System.getenv("ITEMS_ICONS") ?: ""
    val dryRun = findProperty("dryRun")?.toString() ?: System.getenv("ITEMS_DRY_RUN") ?: "false"
    if (items.isNotEmpty()) environment("ITEMS", items)
    environment("ITEMS_WIKI", wiki)
    environment("ITEMS_ICONS", icons)
    environment("ITEMS_DRY_RUN", dryRun)
}

dependencies {
    implementation(libs.jsonSimple)
    implementation(libs.jsoup)
    implementation(libs.coroutines)
    testImplementation(libs.test.junit)
    testImplementation(libs.jsoup)
}
