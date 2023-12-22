package chapter7.item42;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Item42 {
    public static void main(String[] args) {
        List<String> words = new ArrayList<>();
        Collections.sort(words, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.compare(o1.length(), o2.length());
            }
        });

        Collections.sort(words, (s1, s2) -> Integer.compare(s1.length(), s2.length()));
    }
}
