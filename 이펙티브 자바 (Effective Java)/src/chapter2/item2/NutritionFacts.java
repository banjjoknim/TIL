package chapter2.item2;

public class NutritionFacts {
//    ====================================================== 점층적 생성자 패턴
//    private final int servingSize; // 필수
//    private final int servings; // 선택
//    private final int calories;
//    private final int fat;
//    private final int sodium;
//    private final int carbohydrate;
//
//    // ...
//
//    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium) {
//        this(servingSize, servings, calories, fat, sodium, 0);
//    }
//
//    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium, int carbohydrate) {
//        this.servingSize = servingSize;
//        this.servings = servings;
//        this.calories = calories;
//        this.fat = fat;
//        this.sodium = sodium;
//        this.carbohydrate = carbohydrate;
//    }

//    ====================================================== 자바빈즈 패턴
//    private int servingSize;
//    private int servings;
//    private int calories;
//    private int fat;
//    private int sodium;
//    private int carbohydrate;
//
//    public NutritionFacts() {
//    }
//
//    public void setServingSize(int servingSize) {
//        this.servingSize = servingSize;
//    }
//
//    public void setServings(int servings) {
//        this.servings = servings;
//    }
//
//    public void setCalories(int calories) {
//        this.calories = calories;
//    }
//
//    public void setFat(int fat) {
//        this.fat = fat;
//    }
//
//    public void setSodium(int sodium) {
//        this.sodium = sodium;
//    }
//
//    public void setCarbohydrate(int carbohydrate) {
//        this.carbohydrate = carbohydrate;
//    }

//  ================================================================= 빌더 패턴
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    public static class Builder {
        // 필수 매개변수
        private final int servingSize;
        private final int servings;

        // 선택 매개변수 - 기본값으로 초기화한다.
        private int calories = 0;
        private int fat = 0;
        private int sodium = 0;
        private int carbohydrate = 0;

        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }

        public Builder calories(int val) {
            calories = val;
            return this;
        }

        public Builder fat(int val) {
            fat = val;
            return this;
        }

        public Builder sodium(int val) {
            sodium = val;
            return this;
        }

        public Builder carbohydrate(int val) {
            carbohydrate = val;
            return this;
        }

        public NutritionFacts build() {
            return new NutritionFacts(this);
        }
    }

    private NutritionFacts(Builder builder) {
        servingSize = builder.servingSize;
        servings = builder.servings;
        calories = builder.calories;
        fat = builder.fat;
        sodium = builder.sodium;
        carbohydrate = builder.carbohydrate;
    }
}
