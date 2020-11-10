package Part1.Chapter3;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

// 메서드 참조
// p.118
public class Quiz3_6 {
    ToIntFunction<String> stringToInt = (String s) -> Integer.parseInt(s);
    ToIntFunction<String> stringToIntAnswer = Integer::parseInt;

    BiPredicate<List<String>, String> contains = (list, element) -> list.contains(element);
    BiPredicate<List<String>, String> containsAnswer = List::contains;

    Predicate<String> startsWithNumber = (String string) -> this.startsWithNumber(string);
    Predicate<String> startsWithNumberAnswer = this::startsWithNumber;

    private boolean startsWithNumber(String string) {
        return '0'<= string.charAt(0) && string.charAt(0) <= '9';
    }
}
