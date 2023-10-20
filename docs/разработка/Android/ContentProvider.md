единственный правильный способ совместного использования данных между приложениями в [[Android]]
Регистрируется в Manifest и должен иметь уникальный `authorities` параметр, если в системе уже будет ContentProvider с заданным `autorities`
то приложение не установится

ContentProvider получает данные при помощи [[ContentResolver]]

Activity <-> CursorLoader <-> ContentResolver <-> ContentProvider <-> Data
