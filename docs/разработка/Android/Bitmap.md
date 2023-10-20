объект который хранит в себе изображение

Получение файла

- `BitmapFactory.decodeFile(...)`
- `BitmapFactory.decodeResource(...)`
- `BitmapFactory.decodeStream(...)`
  Создание на основе другого
- `Bitmap.createBitmap(...)`
- `Bitmap.createScaleBitmap(...)`
  BitmapFactory.Options
- inJustDecodeBounds - вернет только информацию о битмапе
- inSampleSize - уменьшает изображение в X раз (кратно 2)
- inPreferredConfig(...) - задает конфиг типа RGB_565

Файл с набором байт 1024x1024 занимал бы 3 мегабайта
поэтому есть кодировщики как
ARGB888 (32bit)
RGB (32bit) - 12 байта на пиксель float [0.0 - 1.0]
RGB565 (16bit) - на зеленый больше цветов (особенность человеческого глаза)
RGB888 (24bit) - R[0-255]G[0-255]B[0-255]
