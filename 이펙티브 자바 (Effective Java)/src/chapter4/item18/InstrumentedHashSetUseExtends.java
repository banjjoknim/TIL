package chapter4.item18;

import java.util.Collection;
import java.util.HashSet;

class InstrumentedHashSetUseExtends<E> extends HashSet {
    private int addCount = 0; // 추가된 원소의 수

    public InstrumentedHashSetUseExtends() {
    }

    public InstrumentedHashSetUseExtends(int initCap, float loadFactor) {
        super(initCap, loadFactor);
    }

    @Override
    public boolean add(Object o) {
        return super.add(o);
    }

    @Override
    public boolean addAll(Collection c) {
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }

}
