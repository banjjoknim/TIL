package Part1.Chapter3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;

// 3.3 람다 활용 : 실행 어라운드 패턴
public class ExecuteAroundPattern {

    class Apple {
        private int weight;
        private String color;

        public Apple(int weight, String color) {
            this.weight = weight;
            this.color = color;
        }

        public int getWeight() {
            return weight;
        }

        public String getColor() {
            return color;
        }
    }

    public static String processFile(BufferedReaderProcessor p) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
            return p.process(br);
        }
    }

    @FunctionalInterface
    public interface BufferedReaderProcessor {
        String process(BufferedReader b) throws IOException;
    }

    public static void execute(Runnable r) {
        r.run();
    }

    public Callable<String> fetch() {
        return () -> "Tricky example ;-)";
    }

    public static void main(String[] args) throws IOException {
        String result = processFile((BufferedReader br) -> br.readLine());
        execute(() -> {
        });
        // Predicate<Apple> p = (Apple a) -> a.getWeight(); // -> Predicate는 boolean 타입을 반환하므로 틀린 람다표현식이다.
    }

    // 실행 어라운드 패턴을 적용하는 네 단계의 과정
    // 1. 필요한 메서드를 만든다
    // 2. 메서드에 전달할 인수인 함수형 인터페이스를 만든다
    // 3. 함수형 인터페이스를 만들고 메서드에 인수로 추가한 뒤, 추가한 인터페이스의 추상 메서드를 사용하도록 변경.
    // 4. 인수로 추가한 함수형 인터페이스의 자리에 람다식을 넣어 함수형 인터페이스의 추상 메서드를 구현한다.

    // 함수형 인터페이스의 추상메서드는 람다 표현식의 시그니처를 묘사한다.
    // 함수형 인터페이스의 추상 메서드 시그니처를 함수 디스크립터라고 한다.
}
