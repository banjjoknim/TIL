package Chapter8;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

// p.280
public class Quiz8_1 {
    public static void main(String[] args) {
        // 다음 코드를 실행한 결과는?
        List<String> actors = List.of("Keanu", "Jessica");
//        actors.set(0, "Brad");
//        System.out.println(actors);

        // java.lang.UnsupportedOperationException이 발생한다. List.of로 만든 컬렉션은 바꿀 수 없기 때문이다.
        Map<String, Long> moviesToCount = new HashMap<>();
        String movieName = "JamesBond";
//        long count = moviesToCount.get(movieName);
//        if(count == 0) {
//            moviesToCount.put(movieName, 1L);
//        } else {
//            moviesToCount.put(movieName, count + 1);
//        }
        moviesToCount.merge(movieName, 1L, (key, count) -> count + 1);
    }
}
