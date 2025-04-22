package Tutorials;

import java.util.*;
import java.util.stream.Collectors;

public class Tutorial_9 {
    public static <E> void printArray(E[] elements){
        for (E element : elements){
            System.out.println(element);
        }
    }

    public static void sort(List<Integer> array){
        Collections.shuffle(array);
        Collections.sort(array);
        System.out.println(Collections.max(array) + Collections.min(array));
    }

    public static List<String> operateStrings(List<String> strings){
        List<String> result = strings.stream().filter(string -> string.length() >= 5).map(String::toUpperCase).collect(Collectors.toList());
        return result;
    }

    public static long countStrings(List<String> strings){
        long count = strings.stream().filter(string -> string.length() >= 5).count();
        return count;
    }

    public static String findDay(int day){
        String dayType = switch(day){
            case 1, 2, 3, 4, 5 -> "Weekday";
            case 6, 7 -> "Weekend";
            default -> "Invalid";
        };
        return dayType;
    }
}
