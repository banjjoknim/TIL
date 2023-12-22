# 아이템 2. 생성자에 매개변수가 많다면 빌더를 고려하라

## 점층적 생성자 패턴(telescoping constructor pattern)

- 점층적으로 필수 매개변수와 선택 매개변수를 모두 받는 생성자까지 생성자를 늘려가는 방식이다.

```java
public class NutritionFacts {
    private final int servingSize; // 필수
    private final int servings; // 필수
    private final int calories; // 선택
    private final int fat; // 선택
    private final int sodium; // 선택
    private final int carbohydrate; // 선택

    // ...

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium) {
        this(servingSize, servings, calories, fat, sodium, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium, int carbohydrate) {
        this.servingSize = servingSize;
        this.servings = servings;
        this.calories = calories;
        this.fat = fat;
        this.sodium = sodium;
        this.carbohydrate = carbohydrate;
    }
}

public class Client {
    public static void main(String[] args) {
        NutritionFacts coke =
                new NutritionFacts(240, 8, 100, 0, 35, 27); // 각각의 매개변수가 무엇을 의미하는지 알기 어렵고, 작성도 쉽지 않다.
    }
}
```

- 점층적 생성자 패턴도 쓸 수는 있지만, 매개변수 개수가 많아지면 클라이언트 코드를 작성하거나 읽기 어렵다.
- 점층적 생성자 패턴은 확장하기 어렵다.

## 자바빈즈 패턴(JavaBeans pattern)

- 선택 매개변수가 많을 때 활용할 수 있다.
- 매개변수가 없는 생성자로 객체를 만든 후, 세터(setter) 메서드들을 호출해 원하는 매개변수의 값을 설정하는 방식이다.

```java
public class Client {
    public static void main(String[] args) {
        NutritionFacts coke = new NutritionFacts();
        coke.setServingSize(240);
        coke.setServings(8);
        coke.setCalories(100);
        coke.setSodium(35);
        coke.setCarbohydrate(27);
    }
}
```

- 자바빈즈 패턴에서는 객체 하나를 만들려면 메서드를 여러 개 호출해야 한다.
- 객체가 완전히 생성되기 전까지는 일관성(consistency)이 무너진 상태에 놓이게 된다.
- 자바빈즈 패턴을 사용하면 일관성이 깨지고 그 결과, 객체를 불변으로 만들 수 없게 되고 스레드 안전성을 얻으려면 프로그래머가 추가 작업을 해줘야만 한다.

## 빌더 패턴(builder pattern)

- 클라이언트는 필요한 객체를 직접 만드는 대신, 필수 매개변수만으로 생성자(혹은 정적 팩터리)를 호출해 빌더 객체를 얻는다.
- 그 다음 빌더 객체가 제공하는 일종의 세터 메서드들로 원하는 선택 매개변수들을 설정한다.
- 마지막으로 매개변수가 없는 build 메서드를 호출해 우리에게 필요한 (보통은 불변인) 객체를 얻는다.
- 빌더는 생성할 클래스 안에 정적 멤버 클래스로 만들어두는 게 보통이다.

```java
public class NutritionFacts {
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

public class Client {
    public static void main(String[] args) {
        NutritionFacts coke = new NutritionFacts.Builder(240, 8)
                .calories(100)
                .sodium(35)
                .carbohydrate(27)
                .build();
    }
}
```

- NutritionFacts 클래스는 불변이며, 모든 매개변수의 기본값들을 한곳에 모아뒀다.
- 빌더의 세터 메서드들은 빌더 자신을 반환하기 때문에 연쇄적으로 호출할 수 있다.
- 이렇게 메서드 호출이 흐르듯 연결된다는 뜻으로 플루언트 API(fluent API) 혹은 메서드 연쇄(method chaining)라 한다.
- 빌더 패턴은 계층적으로 설계된 클래스와 함께 쓰기에 좋다.
- 빌더 패턴은 상당히 유연하다. 빌더 하나로 여러 객체를 순회하면서 만들 수 있고, 빌더에 넘기는 매개변수에 따라 다른 객체를 만들 수도 있다.

- 생성자로는 누릴 수 없는 사소한 이점으로, 빌더를 이용하면 가변인수(varargs) 매개변수를 여러 개 사용할 수 있다.

## 핵심 정리

**생성자나 정적 팩터리가 처리해야 할 매개변수가 많다면 빌더 패턴을 선택하는 게 더 낫다.** 매개변수 중 다수가 필수가 아니거나 같은 타입이면 특히 더 그렇다. 빌더는 점층적 생성자보다 클라이언트 코드를 읽고 쓰기가
훨씬 간결하고, 자바빈즈보다 훨씬 안전하다.

---

# 참고자료

