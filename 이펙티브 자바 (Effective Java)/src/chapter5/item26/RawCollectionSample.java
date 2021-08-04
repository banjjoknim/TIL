package chapter5.item26;

import java.util.Collection;

class RawCollectionSample {
    private final Collection stamps = null; // 편의상 null을 할당했다.

    void sample() {
        stamps.add(new Coin()); // "unchecked call" 경고를 내뱉는다.
    }
}
