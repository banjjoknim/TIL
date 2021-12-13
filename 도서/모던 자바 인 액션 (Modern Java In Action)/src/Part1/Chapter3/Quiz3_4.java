package Part1.Chapter3;

import java.util.function.IntFunction;

// 함수형 인터페이스
// p.106
public class Quiz3_4 {
    //1. T -> R
    @FunctionalInterface
    public interface Function<T, R> {
        R apply(T t);
    }

    Function<String, Integer> getLength = (String s) -> s.length();

    //2. (int, int) -> int
    IntFunction intFunction = (int a) -> a + 1;

    //3. T -> void
    @FunctionalInterface
    public interface Consumer<T> {
        void apply(T t);
    }

    Consumer<String> showLength = (String s) -> System.out.println(s.length());

    //4. () -> T
    @FunctionalInterface
    public interface Supplier<T> {
        T get();
    }

    Supplier<Integer> get = () -> 10;

    @FunctionalInterface
    public interface Callable<T> {
        T call();
    }

    Supplier<String> call = () -> "get!";

    //5. (T, U) -> R
    @FunctionalInterface
    public interface Bi<T, U, R> {
        R get();
    }

}
