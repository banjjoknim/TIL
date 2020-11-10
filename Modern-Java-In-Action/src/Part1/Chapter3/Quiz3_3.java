package Part1.Chapter3;

import java.util.concurrent.Callable;

// 어디에 람다를 사용할 수 있는가?
// p.96
public class Quiz3_3 {

    //1. O
    public void execute(Runnable r) {
        r.run();
    }

    //2. O
    public Callable<String> fetch() {
        return () -> "Tricky example ;-)";
    }

    //3. X
    // Predicate<Apple> p = (Apple apple) -> apple.getWeight();
}
