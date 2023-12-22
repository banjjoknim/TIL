package Chapter6;

import Chapter4.Dish;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;

// partioningBy 사용
// p.221
public class Quiz6_2 {
    public static void main(String[] args) {
        List<Dish> menu = Arrays.asList(
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
        // groupingBy 컬렉터와 마찬가지로 partioningBy 컬렉터도 다른 컬렉터와 조합해서 사용할 수 있다.
        // 특히 두 개의 partioningBy 컬렉터를 이용해서 다수준 분할을 수행할 수 있다.
        // 다음 코드의 다수준 분할 결과를 예측해보자.

        // 1. {false={false=[chicken, prawns, salmon], true=[pork, beef]}, true={false=[rice, season fruit], true=[french fries, pizza]}}
        menu.stream().collect(partitioningBy(Dish::isVegetarian, partitioningBy(d -> d.getCalories() > 500)));

        // 2. partitioningBy는 Predicate<T>를 인수로 받기 때문에 컴파일이 불가능하다.
//        menu.stream().collect(partitioningBy(Dish::isVegetarian, partitioningBy(Dish::getType)));

        // 3. {false=5, true=4}
        menu.stream().collect(partitioningBy(Dish::isVegetarian, counting()));

        Map<Boolean, Dish> mostCaloricPartionedByVegetarian =
                menu.stream().collect(partitioningBy(Dish::isVegetarian,
                        collectingAndThen(maxBy(comparingInt(Dish::getCalories)),
                                Optional::get)));

        Map<Boolean, String> stringMap = menu.stream()
                .collect(partitioningBy(Dish::isVegetarian, mapping(Dish::getName, joining())));

        menu.stream().collect(collectingAndThen(toList(), List::size));
    }

}
