package chapter3.item14;

import java.util.Comparator;

import static java.util.Comparator.comparingInt;

class PhoneNumber implements Comparable<PhoneNumber> {
    private Short areaCode;
    private Short prefix;
    private Short lineNum;

//    ================================================== 기본 타입 필드가 여럿일 때의 비교자
//    @Override
//    public int compareTo(PhoneNumber phoneNumber) {
//        int result = Short.compare(areaCode, phoneNumber.areaCode); // 가장 중요한 필드
//        if (result == 0) {
//            result = Short.compare(prefix, phoneNumber.prefix); // 두 번째로 중요한 필드
//            if (result == 0) {
//                result = Short.compare(lineNum, phoneNumber.lineNum); // 세 번째로 중요한 필드
//            }
//        }
//        return result;
//    }

    //    ================================================== 비교자 생성 메서드를 활용한 비교자
    private static final Comparator<PhoneNumber> COMPARATOR = comparingInt((PhoneNumber phoneNumber) -> phoneNumber.areaCode)
            .thenComparingInt(phoneNumber -> phoneNumber.prefix)
            .thenComparingInt(phoneNumber -> phoneNumber.lineNum);

    public int compareTo(PhoneNumber phoneNumber) {
        return COMPARATOR.compare(this, phoneNumber);
    }
}
