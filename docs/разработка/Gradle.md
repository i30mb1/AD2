[[Gradle Assemble Task.png]]

- gradle assembleDebug - создать APK debug файл
- gradle install Debug - устанавливает APK на устройство
- gradle build --scan - анализ проекта
- gradle assembleDebug --profile - простой анализ

Флаги

- rerun-tasks - заставить перезапустить все таски не используя кэш
- --q - тихий мод

все содержимое buildSrc попадает в classpath build.gradle.kts
