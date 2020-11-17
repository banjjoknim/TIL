package Chapter8;

import java.util.HashMap;
import java.util.Map;

// p.289
public class Quiz8_2 {
    public static void main(String[] args) {
        // 다음 코드가 어떤 작업을 수행하는지 파악한 다음 코드를 단순화할 수 있는 방법을 설명하시오.
        Map<String, Integer> movies = new HashMap<>();
        movies.put("JamesBond", 20);
        movies.put("Matrix", 15);
        movies.put("Harry Potter", 5);
//        Iterator<Map.Entry<String, Integer>> iterator = movies.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, Integer> entry = iterator.next();
//            if (entry.getValue() < 10) {
//                iterator.remove();
//            }
//        }
//        System.out.println(movies);

        // removeIf를 사용할 수 있다.
        movies.entrySet().removeIf(entry -> entry.getValue() < 10);
        System.out.println(movies);
    }
}
