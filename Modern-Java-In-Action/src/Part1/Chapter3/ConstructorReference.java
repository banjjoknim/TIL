package Part1.Chapter3;

import java.util.function.BiFunction;

// 3.6.2 생성자 참조
public class ConstructorReference {
    private static final String GREEN = "green";
    // Supplier<Apple> c1 = Apple::new;
    // Apple a1 = c1.get();

    // Function<Integer, Apple> c2 = Apple::new;
    // Apple a2 = c2.apply(110);

    BiFunction<Integer, String, Apple> c3 = Apple::new;
    Apple a3 = c3.apply(110, GREEN);
}
