# Feature: Heroes

Модуль отображает список героев Dota 2, поддерживает поиск/фильтрацию и переход на страницу героя.

## Структура модуля

```
feature/heroes/
├── domain/api/      — публичные интерфейсы и доменные модели (JVM-only)
├── domain/impl/     — бизнес-логика, Room БД, репозитории, реализации UseCases
├── domain/wiring/   — Dagger2 wiring (единственное место, где используется Dagger в domain)
├── ui/              — presentation layer (MVVM, Fragment + RecyclerView)
└── demo/            — standalone-приложение для разработки и тестирования фичи изолированно
```

## Архитектура и поток данных

```
heroes.db (asset, pre-populated SQLite)
    ↓
HeroesDatabase (Room) + HeroesDao
    ↓
HeroesRepositoryImpl (маппинг DB-entity → domain-model)
    ↓
GetHeroesUseCaseImpl
    ↓
GetVOHeroesListUseCase (группировка по атрибуту: Strength / Agility / Intelligence)
    ↓
FilterHeroesUseCase (клиентская фильтрация по имени)
    ↓
HeroesViewModel (StateFlow<List<VOHero>>)
    ↓
HeroesFragment + HeroesListAdapter → RecyclerView (Grid, 3 колонки)
```

## Доменные модели

### Hero
| Поле | Тип | Описание |
|------|-----|----------|
| `name` | String | Имя героя |
| `avatarUrl` | String | `file:///android_asset/heroes/{name}/full.png` |
| `viewedByUser` | Boolean | Просматривался ли герой |
| `mainAttr` | String | Главный атрибут (strength / agility / intelligence) |

### HeroDescription
Детальное описание героя:
- `abilities: List<Ability>` — способности (кулдаун, мана, хоткей, аудио, таланты)
- `mainAttributes: MainAttribute` — базовые характеристики с приростами
- `description`, `history`, `trivia` — текстовые описания
- `Talent(talentLeft, talentLvl, talentRight)` — дерево талантов

## Use Cases (публичный API)

| Use Case | Сигнатура | Статус |
|----------|-----------|--------|
| `GetHeroesUseCase` | `(): Flow<List<Hero>>` | ✅ Реализован |
| `GetHeroByNameUseCase` | `suspend (name): Hero` | ⚠️ TODO |
| `GetHeroDescriptionUseCase` | `(name): Flow<HeroDescription>` | ⚠️ TODO |
| `GetGuideForHeroUseCase` | `(name): Flow<List<Guide>>` | ⚠️ TODO |
| `UpdateStateViewedForHeroUseCase` | `suspend (name)` | ✅ Реализован |
| `GetHeroSpellInputStreamUseCase` | `suspend (spellName): InputStream` | ⚠️ TODO |

## UI слой

### HeroesFragment
- Grid 3 колонки с `GridLayoutManager`
- Item decoration с учётом window insets и % открытия drawer
- Клик по герою → навигация в `heroPageApi`, пометка героя как просмотренного

### HeroesViewModel
- `heroes: StateFlow<List<VOHero>>` (Eager sharing)
- `filterHeroes(filter: String)` — обновляет фильтр
- `refresh()` — принудительное обновление списка
- `updateViewedByUserFieldForHero(name)` — обновление в БД (IO dispatcher)

### VOHero (sealed class для адаптера)
```kotlin
sealed class VOHero {
    data class Body(val name: String, val imageUrl: String, val viewedByUser: Boolean)
    data class Header(val data: HeaderViewHolder.Data)  // Заголовок группы атрибута
}
```

## База данных

- **Room**, версия 1, файл `HeroesDatabase_1.db`
- Таблица `HeroesTable`: `id`, `name`, `mainAttr`, `viewedByUser`
- Pre-populated из asset-файла `heroes.db`
- `HeroesDao`: `getAllHeroes(): Flow<List<HeroDatabase>>`, `updateViewedByUserFieldForName(name)`

## DI (Dagger 2)

```
ApplicationComponent
    └── HeroesModule (domain/wiring)
            └── HeroesDomainComponent
                    ├── HeroesDatabase (singleton)
                    ├── HeroesDao
                    ├── HeroesRepositoryImpl
                    └── 6 UseCase implementations

HeroesFragment
    └── HeroesComponent
            ├── depends on: HeroesDependencies
            ├── HeroesViewModel (AssistedInject factory)
            └── Navigator, Logger, DispatchersProvider
```

### HeroesDependencies (контракт для внешнего модуля)
```kotlin
interface HeroesDependencies : Dependencies {
    val application: Application
    val res: Resources
    val navigator: Navigator
    val logger: Logger
    val getHeroesUseCase: GetHeroesUseCase
    val dispatchersProvider: DispatchersProvider
    val updateStateViewedForHeroUseCase: UpdateStateViewedForHeroUseCase
}
```

## Навигация

- `HeroesProvider` реализует `HeroesApi` — возвращает `HeroesFragment::class.java`
- `HeroesFragmentFactory` — Dagger `FragmentFactory` для создания фрагмента
- Переход на страницу героя: `navigator.heroPageApi.getPagerFragment(heroName)`

## Тесты

### Unit-тесты (HeroesViewModelTest)
- Fake use cases (`GetHeroesUseCaseFake`, `UpdateStateViewedForHeroUseCaseFake`)
- Покрытые сценарии:
  - Список героев при инициализации
  - Фильтрация по частичному совпадению имени
  - Фильтрация по точному имени
  - Сброс фильтра
  - Обновление списка через `refresh()`

### Android-тесты (FragmentTest)
- Запуск `HeroesFragment` в изолированном контейнере

### Demo-приложение
- `HeroesActivityDemo` — запускает `HeroesFragment` без основного приложения
- `ApplicationComponentDemo` — Dagger компонент с mock-зависимостями (пустой Navigator, дефолтный Logger)

## Ключевые файлы

| Файл | Путь |
|------|------|
| Публичные Use Cases | `domain/api/src/main/kotlin/n7/ad2/heroes/domain/` |
| Room DB + DAO | `domain/impl/src/main/kotlin/n7/ad2/heroes/domain/internal/data/` |
| Dagger Wiring | `domain/wiring/src/main/kotlin/n7/ad2/heroes/domain/wiring/` |
| Fragment + ViewModel | `ui/src/main/kotlin/n7/ad2/feature/heroes/ui/internal/` |
| Public UI API | `ui/src/main/kotlin/n7/ad2/feature/heroes/ui/api/` |
| Demo App | `demo/src/main/kotlin/n7/ad2/heroes/demo/` |
| Pre-populated DB | `domain/impl/src/main/assets/heroes.db` |
| Layouts | `ui/src/main/res/layout/` |

## Особенности реализации

- **Offline-first**: данные поставляются вместе с приложением (asset DB), сеть не нужна
- **Фильтрация клиентская**: не через SQL-запросы, что оправдано небольшим размером данных
- **Eager StateFlow**: герои загружаются сразу при подписке
- **Кэш RecyclerView**: pool size 30, item cache 15 для плавной прокрутки
- **Изображения**: ссылки вида `file:///android_asset/heroes/{name}/full.png` — из assets
