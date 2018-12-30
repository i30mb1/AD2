import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyDate {

    private static Date gameDate;
    private static String s;

    public static void main(String[] args) {

        long unixTimeSystem = System.currentTimeMillis() / 1000;

        //просто для хранения даты
        Date date = new Date();
        long unixTimeDate = date.getTime() / 1000;
        //для работы с датами
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1);
        long unixTimeCalendar = calendar.getTimeInMillis() / 1000;
        //для форматирования в нужную дату
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy", new Locale("ru", "RU"));
        //тупо окунаем туда милисикунды и получаем строку которые сформировали
        String era = simpleDateFormat.format(date);
        //преобразуем строку в дату
        try {
            //берём тупо известную нам дату и время и прибавляем текущий год
            gameDate = simpleDateFormat.parse("19:00 16.08" + new SimpleDateFormat(".yyyy", new Locale("ru", "RU")).format(new Date()));
            s = simpleDateFormat.format(gameDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("current: " + era + String.format(Locale.US, " = %d", date.getTime()));
        System.out.println("expect : " + s + " = " + gameDate.getTime());
        long remain = gameDate.getTime() - date.getTime();
        long remainSec = remain / 1000;
        System.out.println("remain : " + String.format(Locale.US, "%02d:%02d:%02d", remainSec / 3600, remainSec / 360, remainSec % 60));

        // %[argument_index$][flag][width][.precision]conversion
        // % - начало инструкций
        // [argument_index$] - целоде десятичное число указывающее на позицию в списке аргументов
        // [flag] - спец символы для форматирования {+ - будет включать знак +, - - выравнивать результат по левому краю, , - устанавливает разделитель для цифр 1,000}
        // [width] - минимально количество символов которое будет выведенно
        // [.precision] - ограничение количество символов после точки
        // conversion - символ {d - целые числа, s - строки, f - строки с плавающей точкой}

        System.out.println("Assault Cuirass (0)".replaceAll("\\([^)]+\\)", ""));
    }
}
