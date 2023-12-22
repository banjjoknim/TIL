package chapter3.item13;

class Yum {

    public Yum() { // 컴파일을 위해 편의상 작성했다.
    }

    public Yum(Yum yum) {
        // ...
    }

    public static Yum newInstance(Yum yum) {
        // ...
        return new Yum(); // 컴파일을 위해 편의상 작성했다.
    }
}
