# 아이템 23. 태그 달린 클래스 보다는 클래스 계층구조를 활용하라

## 태그 달린 클래스

두 가지 이상의 의미를 표현할 수 있으며, 그중 현재 표현하는 의미를 태그 값으로 알려주는 클래스가 있다.

### 태그 달린 클래스 - 클래스 계층구조보다 훨씬 나쁘다!

```java
public class FigureWithTag {
    enum Shape {RECTANGLE, CIRCLE}

    ;

    // 태그 필드 - 현재 모양을 나타낸다.
    final Shape shape;

    // 다음 필드들은 모양이 사각형(RECTANGLE)일 때만 쓰인다.
    double length;
    double width;

    // 다음 필드는 모양이 원(CIRCLE)일 때만 쓰인다.
    double radius;

    // 원용 생성자
    public FigureWithTag(double radius) {
        this.shape = Shape.CIRCLE;
        this.radius = radius;
    }

    // 사각형용 생성자
    public FigureWithTag(double length, double width) {
        this.shape = Shape.RECTANGLE;
        this.length = length;
        this.width = width;
    }

    double area() {
        switch (shape) {
            case RECTANGLE:
                return length * width;
            case CIRCLE:
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError(shape);
        }
    }
}
```

### 태그 달린 클래스 - 단점

- 태그 달린 클래스에는 쓸데없는 코드가 많다.
- 여러 구현이 한 클래스에 혼합돼 있어서 가독성도 나쁘다.
- 다른 의미를 위한 코드도 언제나 함께 하므로 메모리도 많이 사용한다.
- 필드들을 final로 선언하려면 해당 의미에 쓰이지 않는 필드들까지 생성자에서 초기화해야 한다. 즉, 불필요한 코드가 늘어난다.
- 생성자가 태그 필드를 설정하고 해당 의미에 쓰이는 데이터 필드들을 초기화하는 데 컴파일러가 도와줄 수 있는 게 별로 없다. 엉뚱한 필드를 초기화해도 런타임에서 문제가 드러날 뿐이다.
- 또 다른 의미를 추가하려면 코드를 수정해야 한다.
- 인스턴스의 타입만으로는 현재 나타내는 의미를 알 방법이 없다.

**태그 달린 클래스는 장황하고, 오류를 내기 쉽고, 비효율적이다.**
**태그 달린 클래스는 클래스 계층 구조를 어설프게 흉내낸 아류일 뿐이다.**

## 클래스 계층구조

### 태그 달린 클래스를 계층구조로 만드는 방법

- 계층구조의 루트(root)가 될 추상 클래스를 정의한다.
- 태그 값에 따라 동작이 달라지는 메서드들을 루트 클래스의 추상 메서드로 선언한다.
- 태그 값에 상관없이 동작이 일정한 메서드들을 루트 클래스에 일반 메서드로 추가한다.
- 모든 하위 클래스에서 공통으로 사용하는 데이터 필드들도 전부 루트 클래스로 올린다.
- 루트 클래스를 확장한 구체 클래스를 의미별로 하나씩 정의한다.
- 각 하위 클래스에는 각자의 의미에 해당하는 데이터 필드를 넣는다.
- 루트 클래스가 정의한 추상 메서드를 각자의 의미에 맞게 구현한다.

### 태그 달린 클래스를 클래스 계층구조로 변환

```java
public abstract class Figure {
    abstract double area();
}

public class Circle extends Figure {
    final double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * (radius * radius);
    }
}

public class Rectangle extends Figure {
    final double length;
    final double width;

    public Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    @Override
    double area() {
        return length * width;
    }
}
```

### 클래스 계층구조 - 장점

클래스 계층구조는 태그 달린 클래스의 단점을 모두 날려버린다.

- 쓸데없는 코드가 모두 사라진다.
- 각 의미를 독립된 클래스에 담아 관련 없던 데이터 필드를 모두 제거할 수 있으며, 살아남은 필드들은 모두 final이다.
- 각 클래스의 생성자가 모든 필드를 남김없이 초기화하고 추상 메서드를 모두 구현했는지 컴파일러가 확인해준다.
- 실수로 빼먹은 case 문 때문에 런타임 오류가 발생할 일이 없다.
- 루트 클래스의 코드를 건드리지 않고도 독립적으로 계층구조를 확장하고 사용할 수 있다.
- 타입이 의미별로 따로 존재하기 때문에 변수의 의미를 명시하거나 제한할 수 있고, 특정 의미만 매개변수로 받을 수 있다.
- 타입 사이의 자연스러운 계층 관계를 반영할 수 있어서 유연성은 물론 컴파일타임 타입 검사 능력을 높여준다.

## 핵심 정리

- 태그 달린 클래스를 써야 하는 상황은 거의 없다.
- 새로운 클래스를 작성하는 데 태그 필드가 등장한다면 태그를 없애고 계층구조로 대체하는 방법을 생각해보자.
- 기존 클래스가 태그 필드를 사용하고 있다면 계층구조로 리팩터링하는 걸 고민해보자.
