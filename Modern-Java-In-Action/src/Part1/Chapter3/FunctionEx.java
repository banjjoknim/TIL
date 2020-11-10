package Part1.Chapter3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 3.4.3 Function
// 예제 3-4 Function 예제
// Function<T,R> 인터페이스는 제네릭 타입 T를 받아서 제네릭 타입 R을 리턴하는 추상메서드 apply를 정의한다.
public class FunctionEx {

    @FunctionalInterface
    public interface Function<T, R> {
        R apply(T t);
    }

    public <T, R> List<R> map(List<T> list, Function<T, R> f) {
        List<R> result = new ArrayList<>();
        for (T t : list) {
            result.add(f.apply(t));
        }
        return result;
    }

    // [7,2,6]
    List<Integer> l = map(Arrays.asList("lambdas", "in", "action"), (String s) -> s.length());
}
