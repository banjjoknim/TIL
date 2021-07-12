package chapter2.item3;

class Elvis {
//    ======================================== public static final 필드 방식의 싱글턴
//    public static final Elvis INSTANCE = new Elvis();
//
//    private Elvis() {
//    }
//
//    public void doSomeThing() {
//        // ...
//    }

//    ======================================== 정적 팩터리 방식의 싱글턴
    private static final Elvis INSTANCE = new Elvis();

    private Elvis() {

    }

    public static Elvis getInstance() {
        return INSTANCE;
    }
}
