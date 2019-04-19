import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class Algorithms {

    public static void main(String[] args) {
        Integer[] numbers = {31, 15, 8, 3, 1};
        Integer[] numbers2 = {59, 588, 14, 255, 123};

        System.out.println("binary Search in array " + binarySearch(numbers, 1));
        System.out.println("Sort by ABC " + ordinarySort(new ArrayList<>(Arrays.asList(numbers2))));
        System.out.println("Sum all number in array " + sum(new LinkedList<>(Arrays.asList(numbers))));
        System.out.println("Highest number in array " + findHighest(new LinkedList<>(Arrays.asList(numbers))));
        System.out.println("Recursive Sort by ABC" + recursiveSort(new LinkedList<>(Arrays.asList(numbers2))));
        System.out.println("Integer Sum From nums " + Arrays.toString(findIntegerSumFromNums(new int[]{1,45,2,3},4)));
        System.out.println("Find longest substring " + findLongestSubstring("abcafb"));

    }
    // Специальная нотация «0-болъшое> описывает скорость работы алгоритма (в наихудшей скорости)
    // БИНАРНЫЙ ПОИСК O(log n)
    private static Integer binarySearch(Integer[] list, int searchedNumber) { //только для отсартированных масивов или списков
        int low = 0;
        int high = list.length - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int guess = list[mid];
            if (guess == searchedNumber) return guess;
            if (searchedNumber > guess) high = mid - 1;
            else low = mid + 1;
        }
        return null;
    }

    // ПРОСТОЙ ПОИСК O(n)
    private static Integer ordinarySearch(Integer[] list, int searcheNumber) {
        for (Integer number : list) {
            if(number==searcheNumber) return number;
        }
        return null;
    }
    // БЫСТРАЯ СОРТИРОВКА О(n * log n)
    private static LinkedList<Integer> recursiveSort(LinkedList<Integer> list) {
        if (list.size() < 2) {
            return list;
        }
        Integer pivot = list.remove(new Random().nextInt(list.size()-1));
        LinkedList<Integer> less = new LinkedList<>();
        LinkedList<Integer> greater = new LinkedList<>();
        for (Integer num : list) {
            if (num < pivot) less.add(num);
            else greater.add(num);
        }
        LinkedList<Integer> output = new LinkedList<>();
        output.addAll(recursiveSort(less));
        output.add(pivot);
        output.addAll(recursiveSort(greater));
        return output;
    }

    // O(1)
    private static Integer getHashSet() {
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("ogre");
        hashSet.add("anti-mage");
        hashSet.add("legion");

        Iterator iterator = hashSet.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        return 1;
    }

    private static Integer findSmallest(ArrayList<Integer> list) {
        Integer smallest = list.get(0);
        Integer smallest_index = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) < smallest) {
                smallest = list.get(i);
                smallest_index = i;
            }
        }
        return smallest;
    }

    private static LinkedList<Integer> ordinarySort(ArrayList<Integer> list) { //обычная сортировка O(n)
        LinkedList<Integer> newList = new LinkedList<>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Integer smallest = findSmallest(list);
            newList.add(smallest);
            list.remove(smallest);
        }
        return newList;
    }

    private static Integer recursiveFunc(Integer x) { //вызывая сама себя создает стек затрачивает много памяти
        if (x == 1) return 1;
        else return x * (recursiveFunc(x - 1));
    }
    //       Разделяй и Властвуй
    //    1. Определите простейший случай как базовый.
    //    2. Придумайте, как свести задачу к базовому случаю.
    private static Integer sum(LinkedList<Integer> list) {
        if (list.size() == 1) {
            return list.get(0);
        } else {
            return list.pollFirst() + sum(list);
        }
    }

    private static Integer findHighest(LinkedList<Integer> list) { //разделяй и властвуй
        if (list.size() == 2) {
            Integer first = list.pollFirst();
            Integer second = list.pollFirst();
            return first > second ? first : second;
        }
        Integer first = list.pollFirst();
        Integer second = findHighest(list);
        return first > second ? first : second;
    }

    // есть ли сумма 2 чисел в массиве
    private static int[] findIntegerSumFromNums(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), nums[i]};
            }
            map.put(nums[i], nums[i]);
        }
        throw new IllegalArgumentException("No two sum solution");
    }

    // найти количество неповторяющихся символов идущих подряд
    private static int findLongestSubstring(String s) {
        int length = s.length();
        HashSet<Object> set = new HashSet<>();
        int ans = 0, i = 0, currentIterablePosition = 0;
        while (i < length && currentIterablePosition < length) {
            if (!set.contains(s.charAt(currentIterablePosition))) {
                set.add(s.charAt(currentIterablePosition++));
                ans = Math.max(ans, currentIterablePosition - i);
            } else {
                set.remove(s.charAt(i++));
            }
        }
        return ans;
    }

    // реверсируем число
    private static int reverseInteger(int primal) {
        long reverse = 0;
        while (primal != 0) {
            reverse = reverse * 10 + primal % 10;
            primal = primal / 10;
        }
        if (reverse > Integer.MAX_VALUE || reverse < Integer.MIN_VALUE) {
            return 0;
        } else {
            return (int) reverse;
        }
    }

    // является ли числом полиндромом
    private static boolean isPalindrome(int x) {
        if (x < 0 || (x % 10 == 0 && x != 0)) {
            return false;
        }
        int revertedNumber = 0;
        while (x > revertedNumber) {
            revertedNumber = revertedNumber * 10 + x % 10;
            x = x / 10;
        }
        return x == revertedNumber || x == revertedNumber / 10;
    }

}
