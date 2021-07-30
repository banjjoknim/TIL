package chapter4.item21;

class MethodSignature {

    // 메서드 시그니처 -> sum(int, int)
    int sum(int x, int y) {
        return x + y;
    }

//     메서드 시그니처 -> sum(int, int)
//    int sum(int x, int y) { // 컴파일 에러 발생 - 메서드 시그니처가 동일한 메서드가 이미 존재
//        return x + y;
//    }

    // 메서드 시그니처 -> sum2(int, int)
    int sum2(int x, int y) {
        return x + y;
    }

    // 메서드 시그니처 -> sum(int, int, int)
    int sum(int x, int y, int z) { // sum(int, int) 를 오버로딩
        return x + y + z;
    }
}
