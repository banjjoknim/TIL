package Part1.Chapter3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 3.4.1 Predicate
// 예제 3-2 Predicate 예제
// Predicate<T> 인터페이스는 제네릭 타입 T를 받아서 boolean을 리턴하는 추상 메서드 test를 정의한다.
public class PredicateEx {

    @FunctionalInterface
    public interface Predicate<T> {
        boolean test(T t);
    }

    public <T> List<T> filter(List<T> list, Predicate<T> p) {
        List<T> results = new ArrayList<>();
        for (T t : list) {
            if (p.test(t)) {
                results.add(t);
            }
        }
        return results;
    }

    Predicate<String> nonEmptyStringPredicate = (String s) -> !s.isEmpty();
    List<String> nonEmpty = filter(Arrays.asList("1","2","3","4","5"), nonEmptyStringPredicate);
}
