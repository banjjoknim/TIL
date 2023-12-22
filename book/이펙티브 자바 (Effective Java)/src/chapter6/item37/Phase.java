package chapter6.item37;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

enum Phase {
    SOLID, LIQUID, GAS, PLASMA;

//    public enum Transition {
//        MELT, FREEZE, BOIL, CONDENSE, SUBLIME, DEPOSIT;
//
//        // 행은 from의 ordinal을, 열은 to의 ordinal을 인덱스로 쓴다.
//        private static final Transition[][] TRANSITIONS = {
//                {null, MELT, SUBLIME},
//                {FREEZE, null, BOIL},
//                {DEPOSIT, CONDENSE, null}
//        };
//
//        // 한 상태에서 다른 상태로의 전이를 반환한다.
//        public static Transition from(Phase from, Phase to) {
//            return TRANSITIONS[from.ordinal()][to.ordinal()];
//        }
//    }

    enum Transition {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID),
        IONIZE(GAS, PLASMA), DEIONIZE(PLASMA, GAS);

        private final Phase from;
        private final Phase to;

        Transition(Phase from, Phase to) {
            this.from = from;
            this.to = to;
        }

        // 상전이 맵을 초기화한다.
        private static final Map<Phase, Map<Phase, Transition>> m = Stream.of(values())
                .collect(groupingBy(transition -> transition.from,
                        () -> new EnumMap<>(Phase.class),
                        toMap(t -> t.to, t -> t, (x, y) -> y, () -> new EnumMap<>(Phase.class))));

        public static Transition from(Phase from, Phase to) {
            return m.get(from).get(to);
        }
    }
}
