import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class CollectionsAndOperations implements Runnable {

    public static void main(String[] args) {

        byte[] n7 = {78,55};
        byte[] empty = new byte[10];
        ByteArrayInputStream inputStream = new ByteArrayInputStream(n7);
        int read = 0;
        try {
            read = inputStream.read(empty);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (byte b : empty) {
            System.out.println((char) b);
        }


//        speedArrayListLinkedList();
//
//        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
//
//        // Преобразование массив в ArrayList
//        String[] stringArray = {"a", "b", "c", "d", "e"};
//        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(stringArray));
//        // Преобразование ArrayList в массив
//        String[] stringArr = new String[arrayList.size()];
//        arrayList.toArray(stringArr);
//        // Объединение элементов массива в строку
//        String j = String.join(", ", new String[]{"a", "b", "c"});
//        System.out.println(j);


    }

    private static void speedArrayListLinkedList() {

        long time_1 = System.currentTimeMillis();
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            arrayList.add("1");
        }
        long time_2 = System.currentTimeMillis();
        System.out.println("ArrayList speed adding = " + (time_2 - time_1));

        time_1 = System.currentTimeMillis();
        for (int i = 0; i < 100_000; i++) {
            arrayList.get(i);
        }
        time_2 = System.currentTimeMillis();
        System.out.println("ArrayList speed reading = " + (time_2 - time_1));

        time_1 = System.currentTimeMillis();
        ArrayList<String> arrayListWithCapacity = new ArrayList<>(1_000_000);
        for (int i = 0; i < 1_000_000; i++) {
            arrayListWithCapacity.add("1");
        }
        time_2 = System.currentTimeMillis();
        System.out.println("ArrayList with capacity speed adding = " + (time_2 - time_1));

        time_1 = System.currentTimeMillis();
        LinkedList<String> linkedList = new LinkedList<>();
        for (int i = 0; i < 1_000_000; i++) {
            linkedList.add("1");
        }
        time_2 = System.currentTimeMillis();
        System.out.println("LinkedList speed adding = " + (time_2 - time_1));

        time_1 = System.currentTimeMillis();
        for (int i = 0; i < 100_000; i++) {
            linkedList.get(i);
        }
        time_2 = System.currentTimeMillis();
        System.out.println("LinkedList speed reading = " + (time_2 - time_1));

        time_1 = System.currentTimeMillis();
        Iterator<String> iterator = linkedList.iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
        time_2 = System.currentTimeMillis();
        System.out.println("LinkedList speed reading with iterator = " + (time_2 - time_1));

    }

    @Override
    public void run() {

    }
}
