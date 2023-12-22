package Chapter5;

import java.util.function.IntSupplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// 피보나치수열 집합
// p.191
public class Quiz5_4 {
    public static void main(String[] args) {
        // 피보나치수열 집합
        Stream.iterate(new int[]{0, 1}, (arr) -> new int[]{arr[1], arr[0] + arr[1]})
                .limit(20)
                .forEach(t -> System.out.println("(" + t[0] + ", " + t[1] + ")"));

        //피보나치수열
        Stream.iterate(new int[]{0, 1}, (arr) -> new int[]{arr[1], arr[0] + arr[1]})
                .limit(20)
                .map(arr -> arr[0])
                .forEach(System.out::println);

        //generate를 이용한 피보나치수열
        IntSupplier fib = new IntSupplier() {
            private int previous = 0;
            private int current = 1;
            @Override
            public int getAsInt() {
                int oldPrevious = this.previous;
                int nextValue = this.previous + this.current;
                this.previous = this.current;
                this.current = nextValue;
                return oldPrevious;
            }
        };
        IntStream.generate(fib)
                .limit(10)
                .forEach(System.out::println);
    }
}
