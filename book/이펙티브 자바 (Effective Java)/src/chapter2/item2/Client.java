package chapter2.item2;

public class Client {
    public static void main(String[] args) {
//        NutritionFacts coke =
//                new NutritionFacts(240, 8, 100, 0, 35, 27);

//        NutritionFacts coke = new NutritionFacts();
//        coke.setServingSize(240);
//        coke.setServings(8);
//        coke.setCalories(100);
//        coke.setSodium(35);
//        coke.setCarbohydrate(27);

        NutritionFacts coke = new NutritionFacts.Builder(240, 8)
                .calories(100)
                .sodium(35)
                .carbohydrate(27)
                .build();
    }
}
