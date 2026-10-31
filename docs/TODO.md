# TODO

## Хаптик-эффекты

Добавить тактильную отдачу в UI (переключатели, выбор, свайпы, игры в `feature:games`).

- Основной путь: `LocalHapticFeedback.current.performHapticFeedback(HapticFeedbackType.*)` в Compose
  или `View.performHapticFeedback(HapticFeedbackConstants.*)` (`TOGGLE_ON/OFF`, `CONFIRM`, `REJECT` и т.д. — API 34+).
- Поверх — MSDS (Multisensory Design System, звук + вибрация токенами):
  `context.getSystemService(MultisensoryManager::class.java)?.playToken(MultisensoryManager.TOKEN_*)`,
  только при `Build.VERSION.SDK_INT_FULL >= Build.VERSION_CODES_FULL.CINNAMON_BUN_2` (Android 17 QPR2+),
  `getSystemService` может вернуть `null`.
  Док: https://developer.android.com/develop/ui/views/haptics/msds
- Обернуть в один хелпер в `core:ui` (`n7.ad2.core.ui`), чтобы фичи не знали про версии API.
- Проверка: Nothing Phone (3) (A024) на Android 17 / SDK 37.0 — MSDS там пока нет, сработает только fallback.
