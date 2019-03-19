import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class MathOperation {

    public static void main(String[] args) {

        long time_1 = System.currentTimeMillis();
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            list.add("1");
        }
        long time_2 = System.currentTimeMillis();
        System.out.println("arrayList speed = " + (time_2 - time_1));

        time_1 = System.currentTimeMillis();
        ArrayList<String> list2 = new ArrayList<>(1000000);
        for (int i = 0; i < 1000000; i++) {
            list2.add("1");
        }
        time_2 = System.currentTimeMillis();
        System.out.println("arrayList speed = " + (time_2 - time_1));

        time_1 = System.currentTimeMillis();
        LinkedList<String> list3 = new LinkedList<>();
        for (int i = 0; i < 1000000; i++) {
            list3.add("1");
        }
        time_2 = System.currentTimeMillis();
        System.out.println("arrayList speed = " + (time_2 - time_1));

        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();

        //        Преобразование массив в ArrayList
        String[] stringArray = {"a", "b", "c", "d", "e"};
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(stringArray));
        //        Преобразование ArrayList в массив
        String[] stringArr = new String[arrayList.size()];
        arrayList.toArray(stringArr);
        //        Объединение элементов массива в строку
        String j = String.join(", ", new String[]{"a", "b", "c"});
        System.out.println(j);
    }
}
