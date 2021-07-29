package chapter4.item20;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

class AbstractSkeletalConcreteClass {
    static List<Integer> intArrayAsList(int[] a) {
        Objects.requireNonNull(a);

        // 다이아몬드 연산자를 이렇게 사용하는 건 자바 9부터 가능하다.
        // 더 낮은 버전을 사용한다면 <Integer>로 추정하자.
        return new AbstractList<>() {
            @Override
            public Integer get(int index) {
                return a[index]; // 오토박싱
            }

            @Override
            public Integer set(int index, Integer element) {
                int oldElement = a[index];
                a[index] = element;
                return oldElement;
            }

            @Override
            public int size() {
                return a.length;
            }
        };
    }
}
