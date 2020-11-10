package Part1.Chapter3;

// 형식 검사 문제. 다음 코드를 컴파일할 수 없는 이유는?
// p.111
public class Quiz3_5 {
    // Object o = () -> {System.out.println("Tricky example");};
    // Object는 함수형 인터페이스가 아니므로 컴파일이 불가능하다.

    @FunctionalInterface
    public interface Action {
        void act();
    }

    // Runnable과 Action의 함수 디스크립터가 같으므로 누구를 가리키는지가 명확하지 않다. 따라서 컴파일 불가능.
    // Object o1 = () -> System.out.println("Tricky example");

    Object o2 = (Runnable) () -> {
        System.out.println("Tricky example");
    };
    Object o3 = (Action) () -> {
        System.out.println("Tricky example");
    };
    // cast를 통해서 어떤 함수형 인터페이스의 메서드 시그니처가 사용되어야 하는지 명시적으로 구분하도록 람다를 캐스팅할 수 있다.

    Runnable r = () -> {
        System.out.println("Tricky example");
    };

    int portNumber = 1337;
    Runnable r2 = () -> System.out.println(portNumber);
}
