package Chapter7;

import java.util.stream.Stream;

// 문자열 스트림을 탐색하면서 단어 수를 세는 클래스
class WordCounter {
    private final String SENTENCE = "Nel    mezzo del cammin di nostra vita " + "mi ritrovai in una  selva oscura" + "ch   la dritta via era smarrita ";
    private final int counter;
    private final boolean lastSpace;

    public WordCounter(int counter, boolean lastSpace) {
        this.counter = counter;
        this.lastSpace = lastSpace;
    }

    public WordCounter accumulate(Character c) { // 반복 알고리즘처럼 accumulate 메서드는 문자열의 문자를 하나씩 탐색한다.
        if (Character.isWhitespace(c)) {
            return lastSpace ? this : new WordCounter(counter, true);
        } else {
            // 문자를 하나씩 탐색하다 공백 문자를 만나면 지금까지 탐색한 문자를 단어로 간주하여(공백 문자는 제외) 단어 수를 증가시킨다.
            return lastSpace ? new WordCounter(counter + 1, false) : this;
        }
    }

    public WordCounter combine(WordCounter wordCounter) {
        return new WordCounter(counter + wordCounter.counter, // 두 WordCounter의 counter 값을 더한다.
                wordCounter.lastSpace); // counter 값만 더할 것이므로 마지막 공백은 신경 쓰지 않는다.
    }

    public int getCounter() {
        return counter;
    }

    private int countWords(Stream<Character> stream) {
        WordCounter wordCounter = stream.reduce(new WordCounter(0, true),
                WordCounter::accumulate,
                WordCounter::combine);
        return wordCounter.getCounter();
    }
}
