package chapter7.item47;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class Item47 {
    public static void main(String[] args) {
//        for (ProcessHandle ph : ProcessHandle.allProcesses()::iterator) {
//             프로세스를 처리한다.
//        }

        for (ProcessHandle ph : (Iterable<ProcessHandle>) ProcessHandle.allProcesses()::iterator) {
            // 프로세스를 처리한다.
        }

        for (ProcessHandle p : iterableOf(ProcessHandle.allProcesses())) {
            // 프로세스를 처리한다.
        }
    }

    public static <E> Iterable<E> iterableOf(Stream<E> stream) {
        return stream::iterator;
    }

    public static <E> Stream<E> streamOf(Iterable<E> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
