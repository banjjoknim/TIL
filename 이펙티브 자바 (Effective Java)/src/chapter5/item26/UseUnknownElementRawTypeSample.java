package chapter5.item26;

import java.util.Set;

class UseUnknownElementRawTypeSample {
//    static int numElementsInCommon(Set s1, Set s2) {
//        int result = 0;
//        for (Object o1 : s1) {
//            if (s2.contains(o1)) {
//                result++;
//            }
//        }
//        return result;
//    }

    static int numElementsInCommon(Set<?> s1, Set<?> s2) {
        int result = 0;
        for (Object o1 : s1) {
            if (s2.contains(o1)) {
                result++;
            }
        }
        return result;
    }
}
