package Part1.Chapter3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.Predicate;

// 3.7 람다, 메서드 참조 활용하기
public class LambdaAndMethodReference {
    private static final String GREEN = "green";

    public class AppleComparator implements Comparator<Apple> {

        @Override
        public int compare(Apple o1, Apple o2) {
            return Integer.compare(o1.getWeight(), o2.getWeight());
        }
    }

    Comparator<Apple> c = Comparator.comparing((Apple a) -> a.getWeight());

    public static void main(String[] args) {
        List<Apple> inventory = new ArrayList<>();
        inventory.sort((Apple a1, Apple a2) -> Integer.compare(a1.getWeight(), a2.getWeight()));
        inventory.sort((a1, a2) -> Integer.compare(a1.getWeight(), a2.getWeight()));
        inventory.sort(Comparator.comparing(apple -> apple.getWeight()));
        inventory.sort(Comparator.comparing(Apple::getWeight));

        // 3.8 람다 표현식을 조합할 수 있는 유용한 메서드
        // 3.8.1 Comparator 조합
        inventory.sort(Comparator.comparing(Apple::getWeight).reversed());
        inventory.sort(Comparator.comparing(Apple::getWeight)
            .reversed()
            .thenComparing(Apple::getColor));
    }

    // 3.8.2 Predicate 조합
    Predicate<Apple> isRedApple = apple -> "red".equals(apple.getColor());
    Predicate<Apple> notRedApple = isRedApple.negate(); // Predicate 반전. ~~가 아닌 것.
    Predicate<Apple> redAndHeavyApple = isRedApple.and(apple -> apple.getWeight() > 150);
    Predicate<Apple> redAndHeavyAppleOrGreen = isRedApple.and(apple -> apple.getWeight() > 150)
        .or(apple -> GREEN.equals(apple.getColor()));
    // a.or(b).and(c) -> (a || b) && c 와 같다.

    // 3.8.3 Function 조합
    Function<Integer, Integer> f = x -> x + 1;
    Function<Integer, Integer> g = g -> g * 2;
    Function<Integer, Integer> h = f.andThen(g); // f연산 후 그 값으로 g연산을 한다.
    Function<Integer, Integer> h2 = f.compose(g); // g연산 후 그 값으로 f연산을 한다.
    int result = h.apply(1);
    int result2 = h2.apply(1);

    public static class Letter {
        public static String addHeader(String text) {
            return "From Raoul, Mario and Alan: " + text;
        }

        public static String addFooter(String text) {
            return text + " Kind regards";
        }

        public static String checkSpelling(String text) {
            return text.replaceAll("labda", "lambda");
        }
    }

    Function<String, String> addHeader = Letter::addHeader;
    Function<String, String> transformationPipeline = addHeader.andThen(Letter::checkSpelling)
        .andThen(Letter::addFooter);
    Function<String, String> transformationPipeline2 = addHeader.andThen(Letter::addFooter);

    // 3.9 비슷한 수학적 개념
    // 3.9.1 적분
    DoubleFunction<Double> add10 = x -> x + 10;

    public double integrate(DoubleFunction<Double> f, double a, double b) {
        return (f.apply(a) + f.apply(b)) * (b - a) / 2.0;
    }

    double space = integrate(add10, 3, 7);
}
