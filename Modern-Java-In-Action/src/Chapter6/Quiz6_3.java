package Chapter6;

import java.util.List;
import java.util.function.Predicate;

// 자바 8로 takeWhile 흉내내기
// p. 233
public class Quiz6_3 {
    // takeWhile 메서드는 자바 9에서 지원하므로 자바 8에서는 이 기능을 사용할 수 없다. 자바 8에서 takeWhile의 기능을 이용하려면 어떻게 해야 할까?

    // 정렬된 리스트와 프레디케이트를 인수로 받아 프레디케이트를 만족하는 가장 긴 첫 요소 리스트를 반환하도록 직접 takeWhile 메서드를 구현할 수 있다.

    public static <A> List<A> takeWhile(List<A> list, Predicate<A> p) {
        int i = 0;
        for (A item : list) {
            if (!p.test(item)) { // 리스트의 현재 항목이 프레디케이트를 만족하는지 확인
                return list.subList(0, i); // 만족하지 않으면 현재 검사한 항목의 이전 항목 하위 리스트를 반환
            }
            i++;
        }
        return list;
    }

    // 이 메서드를 이용해 isPrime 메서드를 다시 구현할 수 있다. 그리고 이번에도 대상 숫자의 제곱근보다 작은 소수만 검사한다.
    public static boolean isPrime(List<Integer> primes, int candidate) {
        int candidateRoot = (int) Math.sqrt((double) candidate);
        return takeWhile(primes, i -> i <= candidateRoot)
                .stream()
                .noneMatch(p -> candidate % p == 0);
    }

    // 스트림 API와는 달리 직접 구현한 takeWhile 메서드는 적극적으로 동작한다. 따라서 가능하면 noneMatch 동작과 조화를 이룰 수 있도록
    // 자바 9의 스트림에서 제공하는 게으른 버전의 takeWhile을 사용하는 것이 좋다.
}
