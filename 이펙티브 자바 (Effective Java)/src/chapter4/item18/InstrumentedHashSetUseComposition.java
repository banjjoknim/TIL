package chapter4.item18;

import java.util.Collection;
import java.util.Set;

class InstrumentedHashSetUseComposition<E> extends ForwardingSet<E> {
    private int addCount = 0;

    public InstrumentedHashSetUseComposition(Set<E> s) {
        super(s);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}
