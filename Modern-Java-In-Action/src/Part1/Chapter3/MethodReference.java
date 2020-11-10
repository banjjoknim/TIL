package Part1.Chapter3;

import java.util.Arrays;
import java.util.List;

// 3.6 메서드 참조
public class MethodReference {
    public static void main(String[] args) {
        Apple apple = new Apple(10, "green");
        List<String> str = Arrays.asList("a", "b", "A", "B");
        str.sort((s1, s2) -> s1.compareToIgnoreCase(s2));
        str.sort(String::compareToIgnoreCase);
        System.out.println(str);
    }
}

class Apple {
    private int weight;
    private String color;

    public Apple(int weight, String color) {
        this.weight = weight;
        this.color = color;
    }

    public int getWeight() {
        return weight;
    }

    public String getColor() {
        return color;
    }
}
