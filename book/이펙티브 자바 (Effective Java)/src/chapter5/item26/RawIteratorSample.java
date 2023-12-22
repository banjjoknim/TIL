package chapter5.item26;

import java.util.Collection;
import java.util.Iterator;

class RawIteratorSample {
    private final Collection stamps = null; // 편의상 null을 할당했다.

    void sample() {
        for (Iterator i = stamps.iterator(); i.hasNext(); ) {
            Stamp stamp = (Stamp) i.next(); // ClassCastException을 던진다.
            stamp.cancel();
        }
    }
}
