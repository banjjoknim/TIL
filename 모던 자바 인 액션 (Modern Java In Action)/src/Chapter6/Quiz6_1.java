package Chapter6;

import Chapter4.Dish;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.reducing;

// 리듀싱으로 문자열 연결하기
// p.209
public class Quiz6_1 {
    private final List<Dish> menu = Arrays.asList(
            new Dish("pork", false, 800, Dish.Type.MEAT),
            new Dish("beef", false, 700, Dish.Type.MEAT),
            new Dish("chicken", false, 400, Dish.Type.MEAT),
            new Dish("french fries", true, 530, Dish.Type.OTHER),
            new Dish("rice", true, 350, Dish.Type.OTHER),
            new Dish("season fruit", true, 120, Dish.Type.OTHER),
            new Dish("pizza", true, 550, Dish.Type.OTHER),
            new Dish("prawns", false, 300, Dish.Type.FISH),
            new Dish("salmon", false, 450, Dish.Type.FISH)
    );

    //아래 joining 컬렉터를 reducing 컬렉터로 올바르게 바꾼 코드를 모두 선택하시오.
    String shortMenu = menu.stream().map(Dish::getName).collect(Collectors.joining());

    // 1. O
    String shortMenu1 = menu.stream().map(Dish::getName).collect(reducing((s1, s2) -> (s1 + s2))).get();
    // 2. X
    // String shortMenu2 = menu.stream().collect(reducing((d1, d2) -> d1.getName() + d2.getName())).get();
    // 3. O
    String shortMenu3 = menu.stream().collect(reducing("", Dish::getName, (s1, s2) -> s1 + s2));
}
