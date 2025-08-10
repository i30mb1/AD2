# Measure Build Time Plugin

Gradle plugin для измерения производительности сборки с использованием gradle-profiler.

## Возможности

- **Сравнение веток** - автоматическое сравнение времени сборки между текущей веткой и базовой
- **Professional benchmarking** - статистический анализ с warm-up билдами
- **HTML/CSV отчеты** - визуальные графики и детальные данные
- **Git integration** - автоматическое переключение между ветками

## Подключение

```kotlin
// build.gradle.kts
plugins {
    id("measure-build-plugin")
}
```

## Конфигурация

```kotlin
branchComparison {
    baseBranch.set("master")           // базовая ветка для сравнения
    iterations.set(6)                  // количество итераций
    targetTasks.set(listOf(":app:assembleDebug")) // задачи для бенчмарка
    outputDir.set("gradle/profiler/last-output")  // папка с результатами
}
```

## Использование

### Сравнение веток

```bash
./gradlew benchmarkBranchComparison
```

Результаты:

- `gradle/profiler/last-output/benchmark.html` - визуальный отчет
- `gradle/profiler/last-output/benchmark.csv` - данные CSV

## Требования

- gradle-profiler должен быть доступен в `gradle/profiler/bin/`
- Git repository с существующими ветками
- Задачи должны существовать в обеих ветках
