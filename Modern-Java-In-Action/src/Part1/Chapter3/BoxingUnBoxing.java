package Part1.Chapter3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

// 기본형 특화(함수형 인터페이스)
// p.103
// 박싱 : 기본형 -> 참조형 으로 변환
// 언박싱 : 참조형 -> 기본형 으로 변환
public class BoxingUnBoxing {

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        for (int i = 300; i < 400; i++) {
            list.add(i);
        }
        new BoxingUnBoxing().evenNumbers.test(1000);
        new BoxingUnBoxing().oddNumbers.test(1000);
    }

    public interface IntPredicate {
        boolean test(int t);
    }

    IntPredicate evenNumbers = (int i) -> i % 2 == 0;
    Predicate<Integer> oddNumbers = (Integer i) -> i % 2 != 0;
}
