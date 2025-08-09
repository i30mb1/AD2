# Dota 2 Hero Parser

Обновленный парсер героев Dota 2 с поддержкой современных игровых механик.

## Возможности

### Новые поля героев:

- ✅ **Universal attribute** - Новый 4-й тип атрибута
- ✅ **Attack Type** - Melee/Ranged
- ✅ **Complexity** - Рейтинг сложности 1-3 звезды
- ✅ **Roles** - Carry, Support, Initiator и т.д.
- ✅ **Base Stats** - Броня, урон, скорость, здоровье, мана
- ✅ **Aghanim's Shard** - Апгрейды способностей
- ✅ **Innate Abilities** - Врожденные способности
- ✅ **Facets** - Поддержка новой механики (готово к использованию)

### Улучшения стабильности:

- 🛡️ **Robust Error Handling** - Не падает при ошибках отдельных героев
- 🔄 **Multiple Fallback Methods** - Резервные методы парсинга
- ⏱️ **Timeout Protection** - Таймауты для сетевых запросов
- 📊 **Progress Reporting** - Детальный прогресс и статистика
- 🧪 **Test Mode** - Режим для тестирования на ограниченном количестве героев

## Использование

### Запуск парсера:

```kotlin
// В файле HeroParser.kt измените настройки:
private const val TEST_MODE = true   // false для полного парсинга
private const val TEST_HERO_LIMIT = 3  // количество героев для теста

// Затем запустите main функцию
```

### Тестирование:

```bash
# Компиляция
./gradlew :core:parser:build

# Запуск через IDE или создание jar файла
```

## Структура выходных файлов

### heroes.json (обновленная структура):

```json
{
  "name": "Pudge",
  "main_attribute": "Strength",
  "folder_name": "pudge", 
  "attack_type": "Melee",     // НОВОЕ
  "complexity": 2,            // НОВОЕ
  "roles": ["Disabler", "Durable"]  // НОВОЕ
}
```

### description.json (новые поля):

```json
{
  "attackType": "Melee",       // НОВОЕ
  "complexity": 2,             // НОВОЕ
  "roles": [...],              // НОВОЕ
  "baseStats": {               // НОВОЕ
    "armor": -1,
    "damage": "45-51",
    "movementSpeed": 280,
    "health": "120",
    "mana": "75"
  },
  "abilities": [{
    "aghanim_scepter": "...",  // НОВОЕ
    "aghanim_shard": "...",    // НОВОЕ  
    "is_innate": false         // НОВОЕ
  }],
  "facets": [...],             // НОВОЕ (готово к использованию)
  // ... остальные старые поля остаются совместимыми
}
```

## Конфигурация

### Настройки в начале HeroParser.kt:

```kotlin
private const val TEST_MODE = true        // Тестовый режим
private const val TEST_HERO_LIMIT = 3     // Лимит героев для теста
```

### Таймауты и задержки:

- Connection timeout: 10 секунд
- Delay between requests: 500ms (200ms в тестовом режиме)

## Обработка ошибок

Парсер продолжает работу даже при ошибках отдельных героев:

- ❌ Ошибка загрузки одного героя → продолжает с остальными
- ⚠️ Ошибка парсинга одного поля → пропускает поле, сохраняет остальные
- 🔄 Fallback методы для разных способов парсинга
- 📝 Детальное логирование всех ошибок

## Совместимость

- ✅ Обратная совместимость с существующими полями
- ✅ Graceful degradation при отсутствии новых полей
- ✅ Поддержка как старого, так и нового формата сайта
