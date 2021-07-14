package chapter2.item5;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

class SpellChecker {
//    ============================================ 정적 유틸리티를 잘못 사용한 예
//    private static final Lexicon DICTIONARY = null; // 편의상 null로 초기화했다.
//
//    private SpellChecker() {
//
//    } // 객체 생성 방지
//
//    public static boolean isValid(String word) {
//        // someThing
//        return true; // 편의상 이렇게 작성했다.
//    }
//
//    public static List<String> suggestions(String typo) {
//        // someThing
//        return Collections.emptyList(); // 편의상 이렇게 작성했다.
//    }

//  ============================================= 싱글턴을 잘못 사용한 예
//    private final Lexicon dictionary = null;
//
//    private SpellChecker() {
//
//    }
//
//    public static SpellChecker INSTANCE = new SpellChecker();
//
//    public boolean isValid(String word) {
//        return true; // 편의상 이렇게 작성했다.
//    }
//
//    public List<String> suggestions(String typo) {
//        return Collections.emptyList(); // 편의상 이렇게 작성했다.
//    }

    private final Lexicon dictionary;

    public SpellChecker(Lexicon dictionary) {
        this.dictionary = Objects.requireNonNull(dictionary);
    }

    public boolean isValid(String word) {
        return true; // 편의상 이렇게 작성했다.
    }

    public List<String> suggestions(String typo) {
        return Collections.emptyList(); // 편의상 이렇게 작성했다.
    }
}
