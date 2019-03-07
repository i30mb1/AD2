
import java.util.ArrayList;
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
    }
}
