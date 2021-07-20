package chapter3.item11;

class Item11 {
//      ======================================== 최악의 (하지만 적법한) hashCode 구현 - 사용 금지!
//    @Override
//    public int hashCode() {
//        return 42;
//    }

//      ======================================== 최악의 (하지만 적법한) hashCode 구현 - 사용 금지!
//    @Override
//    public int hashCode() {
//        int result = Short.hashCode(value1);
//        result = 31 * result + Short.hashCode(value2);
//        result = 31 * result + Short.hashCode(value3);
//        return result;
//    }

//      ======================================== 한 줄짜리 hashCode 메서드 - 성능이 살짝 아쉽다.
//    @Override
//    public int hashCode() {
//        return Objects.hash();
//    }

//      ======================================== 해시코드를 지연 초기화하는 hashCode 메서드 - 스레드 안정성까지 고려해야 한다.
    private int hashCode; // 자동으로 0으로 초기화된다.

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            // doSomething ...
            hashCode = result;
        }
        return result;
    }
}
