package chapter3.item13;

class Stack implements Cloneable { // 편의상 class 내부를 간소화했다.
    private Object[] elements;

    @Override
    protected Stack clone() {
        try {
            Stack result = (Stack) super.clone();
            result.elements = elements.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
