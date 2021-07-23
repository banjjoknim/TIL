package chapter3.item14;

import java.util.Comparator;

class HashCodeOrder {
    //    ============================================== 해시코드 값의 차를 기준으로 하는 비교자 - 추이성을 위배한다!
    static Comparator<Object> hashCodeOrder = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            return o1.hashCode() - o2.hashCode();
        }
    };


    //    ============================================== 정적 compare 메서드를 활용한 비교자
    static Comparator<Object> staticHashCodeOrder = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            return Integer.compare(o1.hashCode(), o2.hashCode());
        }
    };

    //    ============================================== 비교자 생성 메서드를 활용한 비교자
    static Comparator<Object> comparatorHashCodeOrder = Comparator.comparingInt(o -> o.hashCode());
}
