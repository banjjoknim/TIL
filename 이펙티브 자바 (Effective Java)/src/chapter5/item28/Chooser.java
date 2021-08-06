package chapter5.item28;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class Chooser<T> {
    private final List<T> choiceList;

    public Chooser(Collection<T> choices) {
        choiceList = new ArrayList<>(choices);
    }

    public T choose() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return choiceList.get(random.nextInt(choiceList.size()));
    }
}

//public class Chooser {

//    private final T[] choiceArray;
//    public Chooser(Collection choices) {
//        this.choiceArray = choices.toArray();
//    }
//
//    public Object choose() {
//        ThreadLocalRandom random = ThreadLocalRandom.current();
//        return choiceArray[random.nextInt(choiceArray.length)];

//    }
//    public Chooser(Collection<T> choices) {
//        this.choiceArray = choices.toArray();

//    }
// ...

//}
