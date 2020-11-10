package Part1.Chapter3;

import java.awt.*;

// 생성자 참조
// p.120
public class Quiz3_7 {
    public interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }

    TriFunction<Integer, Integer, Integer, Color> colorFactory = Color::new;
}
