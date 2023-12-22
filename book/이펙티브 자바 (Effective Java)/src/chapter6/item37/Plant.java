package chapter6.item37;

import java.util.*;

import static java.util.stream.Collectors.*;

class Plant {
    enum LifeCycle {ANNUAL, PERENNIAL, BIENNIAL}

    final String name;
    final LifeCycle lifeCycle;

    public Plant(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }

    @Override
    public String toString() {
        return name;
    }

//    public static void main(String[] args) {
//        Set<Plant>[] plantsByLifeCycle = (Set<Plant>[]) new Set[LifeCycle.values().length];
//        for (int i = 0; i < plantsByLifeCycle.length; i++) {
//            plantsByLifeCycle[i] = new HashSet<>();
//        }
//        List<Plant> garden = new ArrayList<>(); // 편의상 빈 리스트로 초기화 했다.
//        for (Plant plant : garden) {
//            plantsByLifeCycle[plant.lifeCycle.ordinal()].add(plant);
//        }
//
//        // 결과 출력
//        for (int i = 0; i < plantsByLifeCycle.length; i++) {
//            System.out.printf("%s : %s%n", Plant.LifeCycle.values()[i], plantsByLifeCycle[i]);
//        }
//    }

    public static void main(String[] args) {
        Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = new EnumMap<>(Plant.LifeCycle.class);
        for (Plant.LifeCycle lifeCycle : Plant.LifeCycle.values()) {
            plantsByLifeCycle.put(lifeCycle, new HashSet<>());
        }
        List<Plant> garden = new ArrayList<>(); // 편의상 빈 리스트로 초기화 했다.
        for (Plant plant : garden) {
            plantsByLifeCycle.get(plant.lifeCycle).add(plant);
        }
        System.out.println(plantsByLifeCycle);

        System.out.println(garden.stream().collect(groupingBy(plant -> plant.lifeCycle)));

        System.out.println(garden.stream().collect(
                groupingBy(plant -> plant.lifeCycle,
                        () -> new EnumMap<>(LifeCycle.class), toSet())));
    }
}
