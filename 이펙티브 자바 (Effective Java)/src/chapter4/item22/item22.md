# 아이템 22. 인터페이스는 타입을 정의하는 용도로만 사용하라

## 인터페이스

- 인터페이스는 자신을 구현한 클래스의 인스턴스를 참조할 수 있는 타입 역할을 한다.
- 클래스가 어떤 인터페이스를 구현한다는 것은 자신의 인스턴스로 무엇을 할 수 있는지를 클라이언트에 이야기해주는 것이다.
- 인터페이스는 오직 이 용도로만 사용해야 한다.

### 상수 인터페이스 안티패턴 - 사용금지!

- 상수 인터페이스란 메더스 없이 상수를 뜻하는 static final 필드로만 가득 찬 인터페이스를 말한다.
- 그리고 이 상수들을 사용하려는 클래스에서는 정규화된 이름(qualified name)을 쓰는 걸 피하고자 그 인터페이스를 구현하곤 한다.

```java
public interface PhysicalConstants {

    // 아보가드로 수 (1/몰)
    static final double AVOGADRO_NUMBER = 6.022_140_857e23;

    // 볼츠만 상수 (J/K)
    static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;

    // 전자 질량 (kg)
    static final double ELECTRON_MASS = 9.109_383_56e-31;
}
```

- **상수 인터페이스 안티패턴은 인터페이스를 잘못 사용한 예다.**
- 클래스 내부에서 사용하는 상수는 외부 인터페이스가 아니라 내부 구현에 해당한다. 따라서 상수 인터페이스를 구현하는 것은 이 내부 구현을 클래스의 API로 노출하는 행위다.
- 클래스가 어떤 상수 인터페이스를 사용하든 사용자에게는 아무런 의미가 없다. 반면, 이는 사용자에게 혼란을 주며 클라이언트 코드가 이 상수들에 종속되게 한다.
    - 만약 다음 릴리스에서 이 상수들을 사용하지 않게 되더라도 바이너리 호환성을 위해 여전히 상수 인터페이스를 구현하고 있어야 한다.
- final이 아닌 클래스가 상수 인터페이스를 구현한다면 모든 하위 클래스의 이름공간이 그 인터페이스가 정의한 상수들로 오염되어 버린다.

## 상수를 공개할 합당한 방법

- 특정 클래스나 인터페이스와 강하게 연관된 상수라면 그 클래스나 인터페이스 자체에 추가해야 한다.
- 열거 타입으로 나타내기 적합한 상수라면 열거 타입으로 만들어 공개하면 된다.
- 또는, 인스턴스화할 수 없는 유틸리티 클래스에 담아 공개하는 방법도 있다.

### 상수 유틸리티 클래스

```java
public class PhysicalConstantsClass {
    private PhysicalConstantsClass() { // 인스턴스화 방지
    }

    // 아보가드로 수 (1/몰)
    public static final double AVOGADRO_NUMBER = 6.022_140_857e23;

    // 볼츠만 상수 (J/K)
    public static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;

    // 전자 질량 (kg)
    public static final double ELECTRON_MASS = 9.109_383_56e-31;
}
```

- 숫자 리터럴에 밑줄을 사용했다. 이 밑줄은 자바 7부터 허용된다.
- 이 밑줄은 숫자 리터럴의 값에는 영향을 주지 않으면서, 가독성은 향상시켜준다.
- 고정소수점 수 또는 부동소수점 수가 5자리 이상이라면 밑줄을 사용하는 걸 고려해보자.
- 십진수 리터럴도 (정소든 부동소수점 수든) 밑줄을 사용해 세 자리씩 묶어주는 것이 좋다.

### 정적 임포트를 사용해 상수 이름만으로 사용하기

```java
import static chapter4.item22.PhysicalConstantsClass.AVOGADRO_NUMBER;

public class Test {
    double atoms(double mols) {
        return AVOGADRO_NUMBER * mols;
    }
    // ... PhysicalConstants를 빈번히 사용한다면 정적 임포트가 값어치를 한다.
}
```

- 유틸리티 클래스에 정의된 상수를 클라이언트에서 사용하려면 클래스 이름까지 함께 명시해야 한다. `ex) PhysicalConstantsClass.AVOGADRO_NUMBER`
- 유틸리티 클래스의 상수를 빈번히 사용한다면 정적 임포트(static import)하여 클래스 이름은 생략할 수 있다.

## 핵심 정리

- 인터페이스는 타입을 정의하는 용도로만 사용해야 한다. 상수 공개용 수단으로 사용하지 말자.
