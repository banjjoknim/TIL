package Part1.Chapter3;

import java.util.Arrays;
import java.util.List;

// 3.4.2 Consumer
// 예제 3-3 Consumer 예제
// Consumer<T> 인터페이스는 제네릭 타입 T를 받아서 void를 리턴하는 추상메서드 accept를 정의한다.
public class ConsumerEx {

    @FunctionalInterface
    public interface Consumer<T> {
        void accept(T t);
    }

    public static <T> void forEach(List<T> list, Consumer<T> c){
        for (T t : list){
            c.accept(t);
        }
    }

    public static void main(String[] args) {
        forEach(Arrays.asList(1,2,3,4,5), (Integer i)->System.out.println(i));
    }
}
