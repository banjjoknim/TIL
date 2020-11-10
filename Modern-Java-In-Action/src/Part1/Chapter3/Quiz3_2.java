package Part1.Chapter3;

// 함수형 인터페이스
// p.93
public class Quiz3_2 {

    //1. O, 오직 하나의 추상 메서드만 가지고 있다.
    public interface Adder {
        int add(int a, int b);
    }

    //2. X, 두개의 추상 메서드(하나는 Adder에서 상속받음)를 포함하고 있다.
    public interface SmartAdder extends Adder {
        int add(double a, double b);
    }

    //3. X, 추상 메서드가 없다.
    public interface Nothing {

    }
}
