# Macro Benchmark Tests

Этот модуль содержит macro benchmark тесты для генерации baseline профилей и измерения производительности.

## Baseline Profile Generator

### Предварительные требования

1. **Android устройство или эмулятор** должен быть подключен и разблокирован
2. **Debug APK** должен быть установлен на устройстве
3. **USB Debugging** должен быть включен

### Запуск тестов

```bash
# 1. Убедитесь что устройство подключено
adb devices

# 2. Установите debug APK (если ещё не установлен)
./gradlew :app:installDebug

# 3. Запустите тесты для генерации baseline profile
./gradlew :macro-benchmark:connectedBenchmarkAndroidTest

# Или запустите отдельный тест
./gradlew :macro-benchmark:connectedBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=n7.ad2.macrobenchmark.StartupBaselineProfileGenerator#generateStartupBaselineProfile
```

### Тесты

#### StartupBaselineProfileGenerator (Рекомендуется)

- `generateStartupBaselineProfile()` - оптимизация запуска приложения
- `generateColdStartBaselineProfile()` - оптимизация холодного старта

#### BaselineProfileGenerator

- `generateMainFlowBaselineProfile()` - полный флоу взаимодействия с приложением

#### ExtendedBaselineProfileGenerator

- `generateExtendedAppFlowBaselineProfile()` - расширенная навигация
- `generateColdStartupBaselineProfile()` - множественные холодные старты

### Устранение неполадок

**Ошибка "No connected devices"**

```bash
adb devices
# Убедитесь что устройство в списке и статус "device"
```

**Ошибка "package n7.ad2 not found"**

```bash
# Установите debug APK
./gradlew :app:installDebug
```

**Тест зависает или не находит элементы**

- Используйте `StartupBaselineProfileGenerator` - он более надёжный
- Убедитесь что экран устройства разблокирован
- Проверьте что приложение запускается вручную

### Результат

После успешного выполнения тестов:

1. Baseline profile будет сохранён в: `app/src/main/baseline-prof.txt`
2. Добавьте этот файл в основной модуль приложения
3. Пересоберите приложение - производительность запуска улучшится на 15-30%

### Проверка результата

```bash
# Посмотрите содержимое сгенерированного профиля
cat app/src/main/baseline-prof.txt
```
