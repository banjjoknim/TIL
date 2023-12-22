# Chapter12. 새로운 날짜와 시간 API
- 자바 API는 복잡한 애플리케이션을 만드는 데 필요한 여러 가지 유용한 컴포넌트를 제공한다.
- 자바 8에서는 지금까지의 날짜와 시간 문제를 개선하는 새로운 날짜와 시간 API를 제공한다.
- 자바 1.0에서는 `java.util.Date` 클래스 하나로 날짜와 시간 관련 기능을 제공했다. 날짜를 의미하는 `Date`라는 클래스의 이름과 달리 `Date` 클래스는 특정 시점을 날짜가 아닌 밀리초 단위로 표현한다.
- 게다가 1900년을 기준으로 하는 오프셋, 0에서 시작하는 달 인덱스 등 모호한 설계로 유용성이 떨어졌다.
- 다음은 자바 9의 릴리스 날짜인 2017년 9월 21일을 가리키는 `Date` 인스턴스를 만드는 코드다.

```java
Date date = new Date(117, 8, 21);
```

다음은 날짜 출력 결과다.

```
Thu Sep 21 00:00:00 CET 2017
```

- 결과가 직관적이지 않으며 `Date` 클래스의 `toString`으로는 반환되는 문자열을 추가로 활용하기가 어렵다.
- 출력 결과에서 알 수 있듯이 `Date`는 `JVM` 기본시간대인 `CET`, 즉 `중앙 유럽시간대(Central European Time)`를 사용했다. 그렇다고 `Date` 클래스가 자체적으로 시간대 정보를 알고 있는 것도 아니다.
- 자바 1.0의 `Date` 클래스에 문제가 있다는 점에는 의문의 여지가 없었지만 과거 버전과 호환성을 깨뜨리지 않으면서 이를 해결할 수 있는 방법이 없었다.
- 결과적으로 자바 1.1에서는 `Date` 클래스의 여러 메서드를 사장(deprecated)시키고 `java.util.Calendar`라는 클래스를 대안으로 제공했다. 안타깝게도 `Calendar` 클래스 역시 쉽게 에러를 일으키는 문제를 가지고 있었다.
- 예를 들어 `Calendar`에서는 1900년도에서 시작하는 오프셋은 없앴지만 여전히 달의 인덱스는 0부터 시작했다.
- 더 안타까운 점은 `Date`와 `Calendar` 두 가지 클래스가 등장하면서 개발자들에게 혼란이 가중된 것이다.
- 게다가 `DateFormat` 같은 일부 기능은 `Date` 클래스에만 작동했다(`DateFormat`은 언어의 종류와 독립적으로 날짜와 시간의 형식을 조절하고 파싱할 때 사용한다).
- `DateFormat`에도 문제가 있었다. 예를 들어 `DateFormat`은 스레드에 안전하지 않다. 즉, 두 스레드가 동시에 하나의 포매터(formatter)로 날짜를 파싱할 때 예기치 못한 결과가 일어날 수 있다.
- 마지막으로 `Date`와 `Calendar`는 모두 가변(mutable) 클래스다. 가변 클래스라는 설계 때문에 유지보수가 아주 어렵다.
- 부실한 날짜와 시간 라이브러리 때문에 많은 개발자는 `Joda-Time` 같은 서드파티 날짜와 시간 라이브러리를 사용했고, 결국 자바 8에서는 `Joda-Time`의 많은 기능을 `java.time` 패키지로 추가했다.

여기서는 새로운 날짜와 시간 API가 제공하는 새로운 기능을 살펴본다. 먼저 사람과 기기에서 사용할 수 있는 날짜와 시간을 생성하는 기본적인 방법을 살펴본 다음에, 날짜 시간 객체를 조작하고, 파싱하고, 출력하거나, 다양한 시간대와 대안 캘린더 등 새로운 날짜와 시간 API를 사용하는 방법을 살펴본다.

---

## 12.1 LocalDate, LocalTime, Instant, Duration, Period 클래스
- `java.time` 패키지는 `LocalDate`, `LocalTime`, `LocalDateTime`, `Instant`, `Duration`, `Period` 등 새로운 클래스를 제공한다.

### 12.1.1 LocalDate와 LocalTime 사용
새로운 날짜와 시간 API를 사용할 때 처음 접하게 되는 것이 `LocalDate`다. `LocalDate` 인스턴스는 시간을 제외한 날짜를 표현하는 불변 객체다. 특히 `LocalDate` 객체는 어떤 시간대 정보도 포함하지 않는다.

정적 팩토리 메서드 `of`로 `LocalDate` 인스턴스를 만들 수 있다. 다음 코드에서 보여주는 것처럼 `LocalDate` 인스턴스는 연도, 달, 요일 등을 반환하는 메서드를 제공한다.

###### 예제 12-1. LocalDate 만들고 값 읽기
```java
LocalDate date = LocalDate.of(2017, 9, 21); // 2017-09-21
int year = date.getYear(); // 2017
Month month = date.getMonth(); // SEPTEMBER
int day = date.getDayOfMonth(); // 21
DayOfWeek dow = date.getDayOfWeek(); //THURSDAY
int len = date.lengthOfMonth(); // 31 (3월의 일 수)
boolean leap = date.isLeapYear(); // false (윤년이 아님)
```

팩토리 메서드 `now`는 시스템 시계의 정보를 이용해서 현재 날짜 정보를 얻는다.

```java
LocalDate today = LocalDate.now();
```

지금부터 살펴볼 다른 날짜와 시간 관련 클래스도 이와 비슷한 기능을 제공한다. `get` 메서드에 `TemporalField`를 전달해서 정보를 얻는 방법도 있다. `TemporalField`는 시간 관련 객체에서 어떤 필드의 값에 접근할지 정의하는 인터페이스다. 열거자 `ChronoField`는 `TemporalField` 인터페이스를 정의하므로  다음 코드에서 보여주는 것처럼 `ChronoField`의 열거자 요소를 이용해서 원하는 정보를 쉽게 얻을 수 있다.

###### 예제 12-2. TemporalField를 이용해서 LocalDate값 읽기
```java
int year = date.get(ChronoField.YEAR);
int month = date.get(ChronoField.MONTH_OF_YEAR);
int day = date.get(ChronoField.DAY_OF_MONTH);
```

다음처럼 내장 메서드 `getYear()`, `getMonthValue()`, `getDayOfMonth()` 등을 이용해 가독성을 높일 수 있다.

```java
int year = date.getYear();
int month = date.getMonthValue();
int day = date.getDayOfMonth();
```

마찬가지로 `13:45:20` 같은 시간은 `LocalTime` 클래스로 표현할 수 있다. 오버로드 버전의 두 가지 정적 메서드 `of`로 `LocalTime` 인스턴스를 만들 수 있다. 즉, 시간과 분을 인수로 받는 `of` 메서드와 시간과 분, 초를 인수로 받는 `of` 메서드가 있다. `LocalDate` 클래스처럼 `LocalTime` 클래스는 다음과 같은 게터 메서드를 제공한다.

###### 예제 12-3. LocalTime 만들고 값 읽기
```java
LocalTime time = LocalTime.of(13, 45, 20);
int hour = time.getHour();
int minute = time.getMinute();
int second = time.getSecond();
```

날짜와 시간 문자열로 `LocalDate`와 `LocalTime`의 인스턴스를 만드는 방법도 있다. 다음처럼 `parse` 정적 메서드를 사용할 수 있다.

```java
LocalDate date = LocalDate.parse("2017-09-21");
LocalTime time = LocalTime.parse("13:45:20");
```

`parse` 메서드에 `DateTimeFormatter`를 전달할 수도 있다. `DateTimeFormatter`의 인스턴스는 날짜, 시간 객체의 형식을 지정한다. `DateTimeFormatter`는 이전에 설명했던 `java.util.DateFormat` 클래스를 대체하는 클래스다. 문자열을 `LocalDate`나 `LocalTime`으로 파싱할 수 없을 때 `parse` 메서드는 `DateTimeParseException (RuntimeException을 상속받은 예외)`을 일으킨다.

### 12.1.2 날짜와 시간 조합
`LocalDateTime`은 `LocalDate`와 `LocalTime`을 쌍으로 갖는 복합 클래스다. 즉, `LocalDateTime`은 날짜와 시간을 모두 표현할 수 있으며 다음 코드에서 보여주는 것처럼 직접 `LocalDateTime`을 만드는 방법도 있고 날짜와 시간을 조합하는 방법도 있다.

###### 예제 12-4. LocalDateTime을 직접 만드는 방법과 날짜와 시간을 조합하는 방법
```java
// 2017-09-21T13:45:20
LocalDateTime dt1 = LocalDateTime.of(2017, Month.SEPTEMBER, 21, 13, 45, 20);
LocalDateTime dt2 = LocalDateTime.of(date, time);
LocalDateTime dt3 = date.atTime(13, 45, 20);
LocalDateTime dt4 = date.atTime(time);
LocalDateTime dt5 = time.atDate(date);
```

`LocalDate`의 `atTime` 메서드에 시간을 제공하거나 `LocalTime`의 `atDate` 메서드에 날짜를 제공해서 `LocalDateTime`을 만드는 방법도 있다. `LocalDateTime`의 `toLocalDate`나 `toLocalTime` 메서드로 `LocalDate`나 `LocalTime` 인스턴스를 추출할 수 있다.

```java
LocalDate date1 = dt1.toLocalDate(); // 2017-09-21
LocalTime time1 = dt1.toLocalTime(); // 13:45:30
```

### 12.1.3 Instant 클래스 : 기계의 날짜와 시간
사람은 보통 주, 날짜, 시간, 분으로 날짜와 시간을 계산한다. 하지만 기계에서는 이와 같은 단위로 시간을 표현하기가 어렵다. 기계의 관점에서는 연속된 시간에서 특정 지점을 하나의 큰 수로 표현하는 것이  가장 자연스러운 시간 표현 방법이다. 새로운 `java.util.Instant` 클래스에서는 이와 같은 기계적인 관점에서 시간을 표현한다. 즉, `Instant` 클래스는 `유닉스 에포크 시간(Unix epoch time - 1970년 1월 1일 0시 0분 0초 UTC)`을 기준으로 특정 지점까지의 시간을 초로 표현한다.

팩토리 메서드 `ofEpochSecond`에 초를 넘겨줘서 `Instant` 클래스 인스턴스를 만들 수 있다. `Instant` 클래스는 나노초(10억분의 1초)의 정밀도를 제공한다. 또한 오버로드된 `ofEpochSecond` 메서드 버전에서는 두 번째 인수를 이용해서 나노초 단위로 시간을 보정할 수 있다. 두 번째 인수에는 0에서 999,999,999 사이의 값을 지정할 수 있다. 따라서 다음 네 가지 `ofEpochSecond` 호출 코드는 같은 `Instant`를 반환한다.

```java
Instant.ofEpochSecond(3);
Instant.ofEpochSecond(3, 0);
Instant.ofEpochSecond(2, 1_000_000_000); // 2초 이후의 1억 나노초(1초)
Instant.ofEpochSecond(4, -1_000_000_000); // 4초 이전의 1억 나노초(1초)
```

`LocalDate` 등을 포함하여 사람이 읽을 수 있는 날짜 시간 클래스에서 그랬던 것처럼 `Instant` 클래스도 사람이 확인할 수 있도록 시간을 표시해주는 정적 팩토리 메서드 `now`를 제공한다. 하지만 `Instant`는 기계 전용의 유틸리티라는 점을 기억하자. 즉, `Instant`는 초와 나노초 정보를 포함한다. 따라서 `Instant`는 사람이 읽을 수 있는 시간 정보를 제공하지 않는다. 예를 들어 다음 코드를 보자.

```java
int day = Instant.now().get(ChronoField.DAY_OF_MONTH);
```

위 코드는 다음과 같은 예외를 일으킨다.

`java.time.temporal.UnsupportedTemporalTypeException: Unsupported field: DayOfMonth`

`Instant`에서는 `Duration`과 `Period` 클래스를 함께 활용할 수 있다.

### 12.1.4 Duration과 Period 정의
지금까지 살펴본 모든 클래스는 `Temporal` 인터페이스를 구현하는데, `Temporal` 인터페이스는 특정 시간을 모델링하는 객체의 값을 어떻게 읽고 조작할지 정의한다. 이번에는 두 시간 객체 사이의 지속시간 `duration`을 만들어볼 차례다. `Duration` 클래스의 정적 팩토리 메서드 `between`으로 두 시간 객체 사이의 지속시간을 만들 수 있다. 다음 코드에서 보여주는 것처럼 두 개의 `LocalTime`, 두 개의 `LocalDateTime`, 또는 두 개의 `Instant`로 `Duration`을 만들 수 있다.

```java
Duration d1 = Duration.between(time1, time2);
Duration d2 = Duration.between(dateTime1, dateTime2);
Duration d3 = Duration.between(instant1, instant2);
```

`LocalDateTime`은 사람이 사용하도록, `Instant`는 기계가 사용하도록 만들어진 클래스로 두 인스턴스는 서로 혼합할 수 없다. 또한 `Duration` 클래스는 초와 나노초로 시간 단위를 표현하므로 `between` 메서드에 `LocalDate`를 전달할 수 없다. 년, 월, 일로 시간을 표현할 때는 `Period` 클래스를 사용한다. 즉, `Period` 클래스의 팩토리 메서드 `between`을 이용하면 두 `LocalDate`의 차이를 확인할 수 있다.

```java
Period tenDays = Period.between(LocalDate.of(2017, 9, 11), LocalDate.of(2017, 9, 21));
```

마지막으로 `Duration`과 `Period` 클래스는 자신의 인스턴스를 만들 수 있도록 다양한 팩토리 메서드를 제공한다. 즉, 다음 예제에서 보여주는 것처럼 두 시간 객체를 사용하지 않고도 `Duration`과 `Period` 클래스를 만들 수 있다.

###### 예제 12-5. Duration과 Period 만들기
```java
Duration threeMinutes = Duration.ofMinutes(3);
Duration threeMinutes = Duration.of(3, ChronoUnit.MINUTES);

Period tenDays = Period.ofDays(10);
Period threeWeeks = Period.ofWeeks(3);
Period twoYearsSixMonthsOneDay = Period.of(2, 6, 1);
```

`표 12-1`은 `Duration`과 `Period` 클래스가 공통으로 제공하는 메서드를 보여준다.

##### 표 12-1. 간격을 표현하는 날짜와 시간 클래스의 공통 메서드
|메서드|정적|설명|
|-|-|-|
|between|네|두 시간 사이의 간격을 생성함|
|from|네|시간 단위로 간격을 생성함|
|of|네|주어진 구성 요소에서 간격 인스턴스를 생성함|
|parse|네|문자열을 파싱해서 간격 인스턴스를 생성함|
|addTo|아니오|현재 값의 복사본을 생성한 다음에 지정된 `Temporal` 객체에 추가함|
|get|아니오|현재 간격 정보값을 읽음|
|isNegative|아니오|간격이 음수인지 확인함|
|isZero|아니오|간격이 0인지 확인함|
|minus|아니오|현재값에서 주어진 시간을 뺀 복사본을 생성함|
|multipliedBy|아니오|현재값에 주어진 값을 곱한 복사본을 생성함|
|negated|아니오|주어진 값의 부호를 반전한 복사본을 생성함|
|plus|아니오|현재값에 주어진 시간을 더한 복사본을 생성함|
|subtractFrom|아니오|지정된 `Temporal` 객체에서 간격을 뺌|

지금까지 살펴본 모든 클래스는 불변이다. 불변 클래스는 함수형 프로그래밍 그리고 스레드 안전성과 도메인 모델의 일관성을 유지하는 데 좋은 특징이다. 하지만 새로운 날짜와 시간 API에서는 변경된 객체 버전을 만들 수 있는 메서드를 제공한다. 예를 들어 기존 `LocalDate` 인스턴스에 3일을 더해야 하는 상황이 발생할 수 있다. 또한 `dd/MM/yyyy` 같은 형식으로 날짜와 시간 포매터를 만드는 방법, 프로그램적으로 포매터를 만드는 방법, 포매터로 날짜를 파싱하고 출력하는 방법도 살펴본다.

---

## 12.2 날짜 조정, 파싱, 포매팅
`withAttribute` 메서드로 기존의 `LocalDate`를 바꾼 버전을 직접 간단하게 만들 수 있다. 다음 코드에서는 바뀐 속성을 포함하는 새로운 객체를 반환하는 메서드를 보여준다. 모든 메서드는 기존 객체를 바꾸지 않는다.

###### 예제 12-6. 절대적인 방식으로 LocalDate의 속성 바꾸기
```java
LocalDate date1 = LocalDate.of(2017, 9, 21); // 2017-09-21
LocalDate date2 = date1.withYear(2011); // 2011-09-21
LocalDate date3 = date1.withDayOfMonth(25); // 2011-09-25
LocalDate date4 = date1.with(ChronoField.MONTH_OF_YEAR, 2); // 2011-02-25
```

`예제 12-6`의 마지막 행에서 보여주는 것처럼 첫 번째 인수로 `TemporalField`를 갖는 메서드를 사용하면 좀 더 범용적으로 메서드를 활용할 수 있다. 마지막 `with` 메서드는 `예제 12-2`의 `get` 메서드와 쌍을 이룬다. 이들 두 메서드는 날짜와 시간 API의 모든 클래스가 구현하는 `Temporal` 인터페이스에 정의되어 있다. `Temporal` 인터페이스는 `LocalDate`, `LocalTime`, `LocalDateTime`, `Instant`처럼 특정 시간을 정의한다. 정확히 표현하자면 `get`과 `with` 메서드로 `Temporal` 객체의 필드값을 읽거나 고칠 수 있다. 어떤 `Temporal` 객체가 지정된 필드를 지원하지 않으면 `UnsupportedTemporalTypeException`이 발생한다. 예를 들어 `Instant`에 `ChronoField.MONTH_OF_YEAR`를 사용하거나 `LocalDate`에 `ChronoField.NANO_OF_SECOND`를 사용하면 예외가 발생한다.

선언형으로 `LocalDate`를 사용하는 방법도 있다. 예를 들어 다음 예제처럼 지정된 시간을 추가하거나 뺄 수 있다.

###### 예제 12-7. 상대적인 방식으로 LocalDate 속성 바꾸기
```java
LocalDate date1 = LocalDate.of(2017, 9, 21); // 2017-09-21
LocalDate date2 = date1.plusWeeks(1); // 2017-09-28
LocalDate date3 = date2.minusYears(6); // 2011-09-28
LocalDate date4 = date3.plus(6, ChronoUnit.MONTHS); // 2012-03-28
```

`예제 12-7`에서는 `with`, `get` 메서드와 비슷한 `plus`, `minus` 메서드를 사용했다. `plus`, `minus` 메서드도 `Temporal` 인터페이스에 정의되어 있다. 이들 메서드를 이용해서 `Temporal`을 특정 시간만큼 앞뒤로 이동시킬 수 있다. 메서드의 인수에 숫자와 `TemporalUnit`을 활용할 수 있다. `ChronoUnit` 열거형은 `TemporalUnit` 인터페이스를 쉽게 활용할 수 있는 구현을 제공한다.

`LocalDate`, `LocalTime`, `LocalDateTime`, `Instant` 등 날짜와 시간을 표현하는 모든 클래스는 서로 비슷한 메서드를 제공한다. `표 12-2`는 이들 공통 메서드를 설명한다.

##### 표 12-2 특정 시점을 표현하는 날짜 시간 클래스의 공통 메서드
|메서드|정적|설명|
|-|-|-|
|from|예|주어진 `Temporal` 객체를 이용해서 클래스의 인스턴스를 생성함|
|now|예|시스템 시계로 `Temporal` 객체를 생성함|
|of|예|주어진 구성 요소에서 `Temporal` 객체의 인스턴스를 생성함|
|parse|예|문자열을 파싱해서 `Temporal` 객체를 생성함|
|atOffset|아니오|시간대 오프셋과 `Temporal` 객체를 합침|
|atZone|아니오|시간대 오프셋과 `Temporal` 객체를 합침|
|format|아니오|지정된 포매터를 이용해서 `Temporal` 객체를 문자열로 변환함(`Instant`는 지원하지 않음)|
|get|아니오|`Temporal` 객체의 상태를 읽음|
|minus|아니오|특정 시간을 뺀 `Temporal` 객체의 복사본을 생성함|
|plus|아니오|특정 시간을 더한 `Temporal` 객체의 복사본을 생성함|
|with|아니오|일부 상태를 바꾼 `Temporal` 객체의 복사본을 생성함|

##### 퀴즈 12-1. LocalDate 조정
다음 코드를 실행했을 때 `date`의 변숫값은?
```java
LocalDate date = LocalDate.of(2014, 3, 18);
date = date.with(ChronoField.MONTH_OF_YEAR, 9);
date = date.plusYears(2).minusDays(10);
date.withYear(2011);
```

`정답 : 2016-09-08`

### 12.2.1 TemporalAdjusters 사용하기
지금까지 살펴본 날짜 조정 기능은 비교적 간단한 편에 속한다. 때로는 다음 주 일요일, 돌아오는 평일, 어떤 달의 마지막 날 등 좀 더 복잡한 날짜 조정 기능이 필요할 것이다. 이때는 오버로드된 버전의 `with` 메서드에 좀 더 다양한 동작을 수행할 수 있도록 하는 기능을 제공하는 `TemporalAdjuster`를 전달하는 방법으로 문제를 해결할 수 있다. 날짜와 시간 API는 다양한 상황에서 사용할 수 있도록 다양한 `TemporalAdjuster`를 제공한다. `예제 12-8`에서 보여주는 것처럼 `TemporalAdjusters`에서 정의하는 정적 팩토리 메서드로 이들 기능을 이용할 수 있다.

###### 예제 12-8. 미리 정의된 TemporalAdjusters 사용하기
```java
import static java.time.temporal.TemporalAdjusters.*;
LocalDate date1 = LocalDate.of(2014, 3, 18); // 2014-03-18
LocalDate date2 = date1.with(nextOrSame(DayOfWeek.SUNDAY)); // 2014-03-23
LocalDate date3 = date2.with(lastDayOfMonth()); // 2014-03-31
```

- `TemporalAdjuster`는 인터페이스며, `TemporalAdjusters`는 여러 `TemporalAdjuster`를 반환하는 정적 팩토리 메서드를 포함하는 클래스이므로 혼동하지 않도록 주의하자. 둘 다 `java.time.temporal` 패키지에 포함되어 있다.

`표 12-3`은 다양한 `TemporalAdjusters`의 팩토리 메서드로 만들 수 있는 `TemporalAdjuster` 리스트를 보여준다.

##### 표 12-3. TemporalAdjusters 클래스의 팩토리 메서드
|메서드|설명|
|-|-|
|dayOfWeekInMonth|서수 요일에 해당하는 날짜를 반환하는 `TemporalAdjuster`를 반환함(음수를 사용하면 월의 끝에서 거꾸로 계산)|
|firstDayOfMonth|현재 달의 첫 번째 날짜를 반환하는 `TemporalAdjuster`를 반환함|
|firstDayOfNextMonth|다음 달의 첫 번째 날짜를 반환하는 `TemporalAdjuster`를 반환함|
|firstDayOfNextYear|내년의 첫 번째 날짜를 반환하는 `TemporalAdjuster`를 반환함|
|firstDayOfYear|올해의 첫 번째 날짜를 반환하는 `TemporalAdjuster`를 반환함|
|firstInMonth|현재 달의 첫 번째 요일에 해당하는 날짜를 반환하는 `TemporalAdjuster`를 반환함|
|lastDayOfMonth|현재 달의 마지막 날짜를 반환하는 `TemporalAdjuster`를 반환함|
|lastDayOfNextMonth|다음 달의 마지막 날짜를 반환하는 `TemporalAdjuster`를 반환함|
|lastDayOfNextYear|다음 해의 마지막 날짜를 반환하는 `TemporalAdjuster`를 반환함|
|lastDayOfYear|올해의 마지막 날짜를 반환하는 `TemporalAdjuster`를 반환함|
|lastInMonth|현재 달의 마지막 요일에 해당하는 날짜를 반환하는 `TemporalAdjuster`를 반환함|
|next|현재 달에서 현재 날짜 이후로 지정한 요일이 처음으로 나타나는 날짜를 반환하는 `TemporalAdjuster`를 반환함|
|previous|현재 달에서 현재 날짜 이후로 지정한 요일이 이전으로 나타나는 날짜를 반환하는 `TemporalAdjuster`를 반환함|
|nextOrSame|현재 날짜 이후로 지정한 요일이 처음으로 나타나는 날짜를 반환하는 `TemporalAdjuster`를 반환함(현재 날짜도 포함)|
|previousOrSame|현재 날짜 이후로 지정한 요일이 이전으로 나타나는 날짜를 반환하는 `TemporalAdjuster`를 반환함(현재 날짜도 포함)|

위 예제에서 확인할 수 있는 것처럼 `TemporalAdjuster`를 이용하면 좀 더 복잡한 날짜 조정 기능을 직관적으로 해결할 수 있다. 그뿐만 아니라 필요한 기능이 정의되어 있지 않을 때는 비교적 쉽게 커스텀 `TemporalAdjuster` 구현을 만들 수 있다. 실제로 `TemporalAdjuster` 인터페이스는 다음처럼 하나의 메서드만 정의한다(하나의 메서드만 정의하므로 함수형 인터페이스다).

###### 예제 12-9. TemporalAdjuster 인터페이스
```java
@FunctionalInterface
public interface TemporalAdjuster {
    Temporal adjustInto(Temporal temporal);
}
```

- 표의 설명만으로 메서드의 기능이 이해되지 않는다면 다양한 예제가 포함되어 있는 [API 문서](http://goo.gl/e1krg1)를 참고하자.

`TemporalAdjuster` 인터페이스 구현은 `Temporal` 객체를 어떻게 다른 `Temporal` 객체로 변환할지 정의한다. 결국 `TemporalAdjuster` 인터페이스를 `UnaryOperator<Temporal>`과 같은 형식으로 간주할 수 있다.

##### 퀴즈 12-2. 커스텀 TemporalAdjuster 구현하기
`TemporalAdjuster` 인터페이스를 구현하는 `NextWorkingDay` 클래스를 구현하시오. 이 클래스는 날짜를 하루씩 다음날로 바꾸는데 이때 토요일과 일요일은 건너뛴다. 즉, 다음 코드를 실행하면 다음날로 이동한다.

```java
date = date.with(new NextWorkingDay());
```

`정답`
```java
public class NextWorkingDay implements TemporalAdjuster {

    @Override
    public Temporal adjustInto(Temporal temporal) {
        DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK)); // 현재 날짜 읽기
        int dayToAdd = 1; // 보통은 하루 추가
        if (dow == DayOfWeek.FRIDAY) {
            dayToAdd = 3; // 그러나 오늘이 금요일이면 3일 추가
        } else if (dow == DayOfWeek.SATURDAY) {
            dayToAdd = 2; // 토요일이면 2일 추가
        }
        return temporal.plus(dayToAdd, ChronoUnit.DAYS); // 적정한 날 수만큼 추가된 날짜를 반환
    }
}
```

`TemporalAdjuster`는 함수형 인터페이스이므로 람다 표현식을 이용할 수 있다. 만일 `TemporalAdjuster`를 람다 표현식으로 정의하고 싶다면 다음 코드에서 보여주는 것처럼 `UnaryOperator<LocalDate>`를 인수로 받는 `TemporalAdjusters` 클래스의 정적 팩토리 메서드 `ofDateAdjuster`를 사용하는 것이 좋다.

```java
TemporalAdjuster nextWorkingDay = TemporalAdjusters.ofDateAdjuster(
    temporal -> {
        ...
    });
date = date.with(nextWorkingDay);
```

자주 사용하는 또 다른 동작으로 각자의 상황에 맞는 다양한 형식으로 날짜와 시간 객체를 출력해야 할 때가 있다. 반면 문자열로 표현된 날짜를 날짜 객체로 다시 변환해야 할 때도 있다. 다음으로는 새로운 날짜와 시간 API로 이와 같은 변환을 수행하는 방법을 살펴본다.

### 12.2.2 날짜와 시간 객체 출력과 파싱
날짜와 시간 관련 작업에서 포매팅과 파싱은 서로 떨어질 수 없는 관계다. 심지어 포매팅과 파싱 전용 패키지인 `java.time.format`이 새로 추가되었다. 이 패키지에서 가장 중요한 클래스는 `DateTimeFormatter`다. 정적 팩토리 메서드와 상수를 이용해서 손쉽게 포매터를 만들 수 있다. `DateTimeFormatter` 클래스는 `BASIC_ISO_DATE`와 `ISO_LOCAL_DATE` 등의 상수를 미리 정의하고 있다. `DateTimeFormatter`를 이용해서 날짜나 시간을 특정 형식의 문자열로 만들 수 있다. 다음은 두 개의 서로 다른 포매터로 문자열을 만드는 예제다.

```java
LocalDate date = LocalDate.of(2014, 3, 18);
String s1 = date.format(DateTimeFormatter.BASIC_ISO_DATE); // 20140318
String s2 = date.format(DateTimeFormatter.ISO_LOCAL_DATE); // 2014-03-18
```

반대로 날짜나 시간을 표현하는 문자열을 파싱해서 날짜 객체를 다시 만들 수 있다. 날짜와 시간 API에서 특정 시점이나 간격을 표현하는 모든 클래스의 팩토리 메서드 `parse`를 이용해서 문자열을 날짜 객체로 만들 수 있다.

```java
LocalDate date1 = LocalDate.parse("20140318", DateTimeFormatter.BASIC_ISO_DATE);
LocalDate date2 = LocalDate.parse("2014-03-18", DateTimeFormatter.ISO_LOCAL_DATE);
```

기존의 `java.util.DateFormat` 클래스와 달리 모든 `DateTimeFormatter`는 스레드에서 안전하게 사용할 수 있는 클래스다. 또한 다음 예제에서 보여주는 것처럼 `DateTimeFormatter` 클래스는 특정 패턴으로 포매터를 만들 수 있는 정적 팩토리 메서드도 제공한다.

###### 예제 12-10. 패턴으로 DateTimeFormatter 만들기
```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
LocalDate date1 = LocalDate.of(2014, 3, 18);
String formattedDate = date1.format(formatter);
LocalDate date2 = LocalDate.parse(formattedDate, formatter);
```

`LocalDate`의 `format` 메서드는 요청 형식의 패턴에 해당하는 문자열을 생성한다. 그리고 정적 메서드 `parse`는 같은 포매터를 적용해서 생성된 문자열을 파싱함으로써 다시 날짜를 생성한다. 다음 코드에서 보여주는 것처럼 `ofPattern` 메서드도 `Locale`로 포매터를 만들 수 있도록 오버로드된 메서드를 제공한다.

###### 예제 12-11. 지역화된 DateTimeFormatter 만들기
```java
DateTimeFormatter italianFormatter = 
        DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.ITALIAN);
LocalDate date1 = LocalDate.of(2014, 3, 18);
String formattedDate = date1.format(italianFormatter); // 18. marzo 2014
LocalDate date2 = LocalDate.parse(formattedDate, italianFormatter);
```

`DateTimeFormatterBuilder` 클래스로 복합적인 포매터를 정의해서 좀 더 세부적으로 포매터를 제어할 수 있다. 즉, `DateTimeFormatterBuilder` 클래스로 대소문자를 구분하는 파싱, 관대한 규칙을 적용하는 파싱(정해진 형식과 정확하게 일치하지 않는 입력을 해석할 수 있도록 체험적 방식의 파서 사용), 패딩, 포매터의 선택사항 등을 활용할 수 있다. 예를 들어 `예제 12-11`에서 사용한 `italianFormatter`를 `DateTimeFormatterBuilder`에 이용하면 프로그램적으로 포매터를 만들 수 있다.

###### 예제 12-12. DateTimeFormatter 만들기
```java
DateTimeFormatter italianFormatter = new DateTimeFormatterBuilder()
        .appendText(ChronoField.DAY_OF_MONTH)
        .appendLiteral(". ")
        .appendText(ChronoField.MONTH_OF_YEAR)
        .appendLiteral(" ")
        .appendText(ChronoField.YEAR)
        .parseCaseInsensitive()
        .toFormatter(Locale.ITALIAN);
```

지금까지 시간과 간격으로 날짜를 만들고, 조작하고, 포맷하는 방법을 살펴봤다. 그러나 날짜와 시간 관련 세부사항을 처리하는 방법(예를 들면 다양한 시간대를 처리하거나 다른 캘린더 시스템 사용)은 아직 살펴보지 않았다. 다음으로는 새로운 날짜와 시간 API로 다양한 시간대와 캘린더를 활용하는 방법을 설명한다.

---

## 12.3 다양한 시간대와 캘린더 활용 방법
지금까지 살펴본 모든 클래스에서는 시간대와 관련한 정보가 없었다. 새로운 날짜와 시간 API의 큰 편리함 중 하나는 시간대를 간단하게 처리할 수 있다는 것이다. 기존의 `java.util.TimeZone`을 대체할 수 있는 `java.time.ZoneId` 클래스가 새롭게 등장햇다. 새로운 클래스를 이용하면 `서머타임(Daylight Saving Time - DST)` 같은 복잡한 사항이 자동으로 처리된다. 날짜와 시간 API에서 제공하는 다른 클래스와 마찬가지로 `ZoneId`는 불변 클래스다.

### 12.3.1 시간대 사용하기
표준 시간이 같은 지역을 묶어서 **시간대(time zone)** 규칙 집합을 정의한다. `ZoneRules` 클래스에는 약 40개 정도의 시간대가 있다. `ZoneId`의 `getRules()`를 이용해서 해당 시간대의 규정을 획득할 수 있다. 다음처럼 지역 ID로 특정 `ZoneId`를 구분한다.

```java
ZoneId romeZone = ZoneId.of("Europe/Rome");
```

지역 ID는 `{지역}/{도시}` 형식으로 이루어지며 `IANA Time Zone Database`에서 제공하는 지역 집합 정보를 사용한다(https://www.iana.org/time-zones 참고). 다음 코드에서 보여주는 것처럼 `ZoneId`의 새로운 메서드인 `toZoneId`로 기존의 `TimeZone` 객체를 `ZoneId` 객체로 변환할 수 있다.

```java
ZoneId zoneId = TimeZone.getDefault().toZoneId();
```

다음 코드에서 보여주는 것처럼 `ZoneId` 객체를 얻은 다음에는 `LocalDate`, `LocalDateTime`, `Instant`를 이용해서 `ZonedDateTime` 인스턴스로 변환할 수 있다. `ZonedDateTime`은 지정한 시간대에 상대적인 시점을 표현한다.

###### 예제 12-13. 특정 시점에 시간대 적용
```java
LocalDate date = LocalDate.of(2014, 3, 18);
ZonedDateTime zdt1 = date.atStartOfDay(romeZone);
LocalDateTime dateTime = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45);
ZonedDateTime zdt2 = dateTime.atZone(romeZone);
Instant instant = Instant.now();
ZonedDateTime zdt3 = instant.atZone(romeZone);
```

`그림 12-1`에서 보여주는 `ZonedDateTime`의 컴포넌트를 보면 `LocalDate`, `LocalTime`, `LocalDateTime`, `ZoneId(ZoneRegion)`의 차이를 쉽게 이해할 수 있다.

##### 그림 12-1. ZonedDateTime의 개념
![ZonedDateTime의 개념](https://user-images.githubusercontent.com/68052095/102091462-32818c00-3e62-11eb-9759-544edebd8f63.png)
[ZonedDateTime 참고 링크](https://perfectacle.github.io/2018/09/26/java8-date-time/)

`ZoneId`를 이용해서 `LocalDateTime`을 `Instant`로 바꾸는 방법도 있다.

다음처럼 변환하는 방법도 있다.

```java
Instant instant = Instant.now();
LocalDateTime timeFromInstant = LocalDateTime.ofInstant(instant, romeZone);
```

기존의 `Date` 클래스를 처리하는 코드를 사용해야 하는 상황이 있을 수 있으므로 `Instant`로 작업하는 것이 유리하다. 폐기된 API와 새 날짜와 시간 API 간의 동작에 도움이 되는 `toInstant()`, 정적 메서드 `fromInstant()` 두 개의 메서드가 있다.

### 12.3.2 UTC/Greenwich 기준의 고정 오프셋
때로는 `UTC(Universal Time Coordinated - 협정 세계시)/GMT(Greenwich Mean Time - 그리니치 표준시)`를 기준으로 시간대를 표현하기도 한다. 예를 들어 '뉴욕은 런던보다 5시간 느리다'라고 표현할 수 있다. `ZoneId`의 서브클래스인 `ZoneOffset` 클래스로 런던의 그리니치 0도 자오선과 시간값의 차이를 표현할 수 있다.

```java
ZoneOffset newYorkOffset = ZoneOffset.of("-05:00");
```

실제로 미국 동부 표준시의 오프셋값은 `-05:00`이다. 하지만 위 예제에서 정의한 `ZoneOffset`으로는 서머타임을 제대로 처리할 수 없으므로 권장하지 않는 방식이다. `ZoneOffset`은 `ZoneId`이므로 `예제 12-13`처럼 `ZoneOffset`을 사용할 수 있다. 또한 `ISO-8601` 캘린더 시스템에서 정의하는 `UTC/GMT`와 오프셋으로 날짜와 시간을 표현하는 `OffsetDateTime`을 만드는 방법도 있다.

```java
LocalDateTime dateTime = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45);
OffsetDateTime dateTimeInNewYork = OffsetDateTime.of(date, newYorkOffset);
```

새로운 날짜와 시간 API는 ISO 캘린더 시스템에 기반하지 않은 정보도 처리할 수 있는 기능을 제공한다.

- `ISO-8601`과 관련한 자세한 정보는 `http://en.wikipedia.org/wiki/ISO_8601`을 참고하자.

### 12.3.3 대안 캘린더 시스템 사용하기
`ISO-8601` 캘린더 시스템은 실질적으로 전 세계에서 통용된다. 하지만 자바 8에서는 추가로 4개의 캘린더 시스템을 제공한다. `ThaiBuddhistDate`, `MinguoDate`,`JapaneseDate`, `HijrahDate` 4개의 클래스가 각각의 캘린더 시스템을 대표한다. 위 4개의 클래스와 `LocalDate` 클래스는 `ChronoLocalDate` 인터페이스를 구현하는데, `ChronoLocalDate`는 임의의 연대기에서 특정 날짜를 표현할 수 있는 기능을 제공하는 인터페이스다. `LocalDate`를 이용해서 이들 4개의 클래스 중 하나의 인스턴스를 만들 수 있다. 일반적으로 다음 코드에서 보여주는 것처럼 정적 메서드로 `Temporal` 인스턴스를 만들 수 있다.

```java
LocalDate date = LocalDate.of(2014, Month.MARCH, 18);
JapaneseDate japaneseDate = JapaneseDate.from(date);
```

또는 특정 `Locale`과 `Locale`에 대한 날짜 인스턴스로 캘린더 시스템을 만드는 방법도 있다. 새로운 날짜와 시간 API에서 `Chronology`는 캘린더 시스템을 의미하며 정적 팩토리 메서드 `ofLocale`을 이용해서 `Chronology`의 인스턴스를 획득할 수 있다.

```java
Chronology japaneseChronology = Chronology.ofLocale(Locale.JAPAN);
ChronoLocalDate now = japaneseChronology.dateNow();
```

날짜와 시간 API의 설계자는 `ChronoLocalDate`보다는 `LocalDate`를 사용하라고 권고한다. 예를 들어 개발자는 1년은 12개월로 이루어져 있으며 1달은 31일 이하이거나, 최소한 1년은 정해진 수의 달로 이루어졌을 것이라고 가정할 수 있다. 하지만 이와 같은 가정은 특히 멀티캘린더 시스템에서는 적용되지 않는다. 따라서 프로그램의 입출력을 지역화하는 상황을 제외하고는 모든 데이터 저장, 조작, 비즈니스 규칙 해석 등의 작업에서 `LocalDate`를 사용해야 한다.

#### 이슬람력
자바 8에 추가된 새로운 캘린더 중 `HijrahDate(이슬람력)`가 가장 복잡한데 이슬람력에서는 변형(variant)이 있기 때문이다. `Hijrah` 캘린더 시스템은 태음월(lunar month)에 기초한다. 새로운 달(month)을 결정할 때 새로운 달(month)을 전 세계 어디에서나 볼 수 있는지 아니면 사우디아라비아에서 처음으로 새로운 달을 볼 수 있는지 등의 변형 방법을 결정하는 메서드가 있다. `withVariant` 메서드로 원하는 변형 방법을 선택할 수 있다. 자바 8에는 `HijrahDate`의 표준 변형 방법으로 `UmmAl-Qura`를 제공한다.

다음 코드는 현재 이슬람 연도의 시작과 끝을 ISO 날짜로 출력하는 예제다.

```java
HijrahDate ramadanDate = HijrahDate.now()
                .with(ChronoField.DAY_OF_MONTH, 1)
                .with(ChronoField.MONTH_OF_YEAR, 9); // 현재 Hijrah 날짜를 얻음. 얻은 날짜를 Ramadan의 첫 번째 날, 즉 9번째 달로 바꿈
        System.out.println("Ramadan starts on " +
                IsoChronology.INSTANCE.date(ramadanDate) + // INSTANCE는 IsoChronology 클래스의 정적 인스턴스임
                " and ends on " +
                IsoChronology.INSTANCE.date(ramadanDate.with(TemporalAdjusters.lastDayOfMonth()))); // Ramadan 1438은 2017-05-26에 시작해서 2017-06-24에 종료됨.
```

---

## 12.4 마치며
- 자바 8 이전 버전에서 제공하는 기존의 `java.util.Date` 클래스와 관련 클래스에서는 여러 불일치점들과 가변성, 어설픈 오프셋, 기본값, 잘못된 이름 결정 등의 설계 결함이 존재했다.
- 새로운 날짜와 시간 API에서 날짜와 시간 객체는 모두 불변이다.
- 새로운 API는 각각 사람과 기계가 편리하게 날짜와 시간 정보를 관리할 수 있도록 두 가지 표현 방식을 제공한다.
- 날짜와 시간 객체를 절대적인 방법과 상대적인 방법으로 처리할 수 있으며 기존 인스턴스를 변환하지 않도록 처리 결과로 새로운 인스턴스가 생성된다.
- `TemporalAdjuster`를 이용하면 단순히 값을 바꾸는 것 이상의 복잡한 동작을 수행할 수 있으며 자신만의 커스텀 날짜 변환 기능을 정의할 수 있다.
- 날짜와 시간 객체를 특정 포맷으로 출력하고 파싱하는 포매터를 정의할 수 있다. 패턴을 이용하거나 프로그램으로 포매터를 만들 수 있으며 포매터는 스레드 안정성을 보장한다.
- 특정 지역/장소에 상대적인 시간대 또는 `UTC/GMT` 기준의 오프셋을 이용해서 시간대를 정의할 수 있으며 이 시간대를 날짜와 시간 객체에 적용해서 지역화할 수 있다.
- `ISO-8601` 표준 시스템을 준수하지 않는 캘린더 시스템도 사용할 수 있다.

---
