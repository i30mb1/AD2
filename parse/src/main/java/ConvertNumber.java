public class ConvertNumber {

    public static final String[] units = {"", "Первый", "Второй", "Третий", "Четвёртый", "Пятый", "Шестой", "Седьмой", "Восьмой",
            "Девятый", "Десятый", "Одиннадцатый", "Двенадцатый", "Тринадцатый", "Четырнадцатый",
            "Пятнадцатый", "Шестнадцатый", "Семьнадцатый", "Восемнадцатый", "Девятнадцатый"};
    public static final String[] tens = {"", "", "Двадцать", "Тридцать", "Сорок",
            "Пятьдесят", "Шестьдесят", "Семьдесят", "Восемьдесят", "Девяносто"};
    public static final String[] sotki = {"", "Сто", "Двесте", "Триста", "Четыреста", "Пятьсот",
            "Шестьсот", "Семьсот", "Восемьсот", "Девятьсот"};

    public static String convert(final int n) {
        if (n < 0) {
            return "Минус " + convert(-n);
        }

        if (n < 20) {
                return units[n];
        }

        if (n < 100) {
            return tens[n / 10] + " " + units[n % 10];
        }

        if (n < 1000) {
            return sotki[n / 100] + " " + convert(n % 100);
        }

        if (n < 1000000) {
            if (n / 1000 == 1) {
                return "Одна Тысяча " + convert(n % 1000);
            } else if (n / 1000 < 5 && n / 1000 >= 9) {
                return convert(n / 1000) + " Тысячи " + convert(n % 1000);
            } else {
                return convert(n / 1000) + " Тысячный " + convert(n % 1000);
            }
        }

        if (n < 10000000) {
            if (n / 1000000 == 1) {
                return "Один Миллион " + convert(n % 100000);
            } else {
                return convert(n / 100000) + " Миллионов " + convert(n % 100000);
            }
        } else {
            if (n / 10000000 == 1) {
                return "Один Триллион " + convert(n % 10000000);
            } else {
                return convert(n / 10000000) + " Триллион " + convert(n % 10000000);
            }
        }
    }
}