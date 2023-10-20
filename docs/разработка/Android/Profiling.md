```Kotlin
Debug.startMethodTracing("trace")
...
Debug.stopMethodTracing()
```

Запись в профайлер участок кода

```Kotlin
Trace.beginSection("trace")
...
Trace.endSection()
```

В logCat показывает когда [[Activity]] отрисовало свой первый кадр

```
ActivityTaskManager: Displayed... +4s555ms...
```

можно вызвать метод `Activity.reportfullyDrawn()` чтобы трекать когда заканчивается отрисовка

```
ActivityTaskManager: Fully draw... +4s555ms...
```
