@file:Suppress("UnusedVariable")

package n7.ad2.parser.algorithm

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateAndStrings {

    private var gameDate: Date? = null
    private var s: String? = null

    @JvmStatic
    fun main(args: Array<String>) {
        val unixTimeSystem = System.currentTimeMillis() / 1000

        // просто для хранения даты
        val date = Date()
        val unixTimeDate = date.time / 1000
        // для работы с датами
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, 1)
        val unixTimeCalendar = calendar.timeInMillis / 1000
        // для форматирования в нужную дату
        val simpleDateFormat = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale("ru", "RU"))
        // тупо окунаем туда милисикунды и получаем строку которые сформировали
        val era = simpleDateFormat.format(date)
        // преобразуем строку в дату
        try {
            // берём тупо известную нам дату и время и прибавляем текущий год
            gameDate = simpleDateFormat.parse("19:00 16.08" + SimpleDateFormat(".yyyy", Locale("ru", "RU")).format(Date()))
            s = simpleDateFormat.format(gameDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        println("current: $era${String.format(Locale.US, " = %d", date.time)}")
        println("expect : $s = ${gameDate?.time}")
        val remain = (gameDate?.time ?: 0) - date.time
        val remainSec = remain / 1000
        println("remain : ${String.format(Locale.US, "%02d:%02d:%02d", remainSec / 3600, remainSec / 360, remainSec % 60)}")

        // read it https://medium.com/better-programming/formatting-strings-with-java-2281d40accce
        // %[argument_index$][flag][width][.precision]conversion
        // % - начало инструкций
        // [argument_index$] - целоде десятичное число указывающее на позицию в списке аргументов
        // [flag] - спец символы для форматирования {'+' будет включать знак +, '-' выравнивать результат по левому краю, ',' устанавливает разделитель для цифр 1,000}
        // [width] - минимально количество символов которое будет выведенно
        // [.precision] - ограничение количество символов после точки
        // conversion - символ {d - целые числа, s - строки, f - строки с плавающей точкой}

        println("Assault Cuirass (0)".replace(Regex("\\([^)]+\\)"), ""))
    }
}
