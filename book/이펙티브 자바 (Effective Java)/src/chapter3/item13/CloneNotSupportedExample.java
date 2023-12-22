package chapter3.item13;

class CloneNotSupportedExample implements Cloneable {
    @Override
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
