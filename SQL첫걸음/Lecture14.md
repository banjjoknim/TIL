# 14강. 날짜 연산

**`날짜 연산`**
```
CURRENT_TIMESTAMP CURRENT_DATE INTERVAL
```

---

## 1. SQL에서의 날짜
- 날짜나 시간 데이터는 수치 데이터와 같이 사칙 연산을 할 수 있다.
- 날짜시간 데이터를 연산하면 결괏값으로 동일한 날짜시간 유형의 데이터를 변환하는 경우도 있으며 기간(간격)의 차를 나타내는 `기간형(interval)` 데이터를 반환하는 경우도 있다.
- 기간형은 `10일간`, `2시간10분`과 같이 시간의 간격을 표현할 수 있다.

### 시스템 날짜
- `시스템 날짜`란 이 같은 하드웨어 상의 시계로부터 실시간으로 얻을 수 있는 일시적인 데이터를 말한다. `RDBMS`에서도 시스템 날짜와 시간을 확인하는 함수를 제공한다.
- 표준 SQL에서는 `CURRENT_TIMESTAMP`라는 긴 이름의 함수로 실행했을 때를 기준으로 시간을 표시한다.
- `CURRENT_TIMESTAMP`는 함수임에도 인수를 필요로 하지 않는다.
- 일반적인 함수와는 달리 인수를 지정할 필요가 없으므로 괄호를 사용하지 않는 특수한 함수이다.

**`시스템 날짜 확인하기`**
```
SELECT CURRENT_TIMESTAMP;
```

- 위의 예에서는 `FROM` 구를 생략했다. `SELECT` 구현만으로도 `SELECT` 명령은 실행됩니다만 `Oracle`과 같은 전통적인 데이터베이스에서는 `FROM` 구를 생략할 수 없으므로 주의해야 한다.
- `CURRENT_TIMESTAMP`는 표준 SQL로 규정되어 있는 함수이다.
- `Oracle`에서는 `SYSDATE` 함수, `SQL Server`에서는 `GETDATE` 함수를 사용해도 시스템 날짜를 확인할 수 있다. 그러나 이들은 표준화되기 전에 구현된 함수인 만큼 사용하지 않는 편이 낫다.

### 날짜 서식
- 날짜 데이터를 데이터베이스에 저장할 경우 `CURRENT_TIMESTAMP`를 사용해 시스템 상의 날짜를 저장할 수 있다.
- 다만 임의의 날짜를 저장하고 싶을 경우에는 직접 날짜 데이터를 지정해야 한다.
- 날짜 서식은 국가별로 다르다. 한국과 일본에서는 연월일을 슬래시나 하이픈으로 구분해 표기하는 경우가 많다.
- 한편 미국에서는 월의 경우 숫자를 대신해 `Jan`, `Feb` 등으로 표기하며 일반적으로 일월년의 순으로 표기한다.
    - 0214/01/25
    - 2014-01-25
    - 25 Jan 2014
- 데이터베이스 제품은 날짜 데이터의 서식을 임의로 지정, 변환할 수 있는 함수를 지원한다.
- `Oracle`의 경우 `TO_DATE` 함수를 사용해 문자열 데이터를 날짜형 데이터로 변환할 수 있으며 서식 또한 별도로 지정할 수 있다.

```
TO_DATE('2014/01/25', 'YYYY/MM/DD')
```

- 여기서 `'YYYY/MM/DD'`가 서식 부분이다.
- `YYYY`가 년, `MM`이 월, `DD가 날`을 의미한다.
- 반대로 날짜형 데이터를 서식에 맞춰 변환해 문자열 데이터로 출력하는 함수도 존재한다.
- `Oracle`의 경우 `TO_CHAR` 함수가 그에 해당한다.

##### 날짜 데이터는 서식을 지정할 수 있다!

---

## 2. 날짜의 덧셈과 뺄셈
- 날짜시간형 데이터는 기간형 수치데이터와 덧셈 및 뺄셈을 할 수 있다.
- 날짜시간형 데이터에 기간형 수치데이터를 더하거나 빼면 날짜시간형 데이터가 반환된다.
- 예를 들어 특정일로부터 1일 후를 계산하고 싶다면 `a + 1 DAY` 라는 식으로 계산할 수 있다. 1일 전이라면 `a - 1 DAY`로 하면 된다.

**`날짜를 연산해 시스템 날짜의 1일 후를 검색`**
```
SELECT CURRENT_DATE + INTERVAL 1 DAY;
```

- `CURRENT_DATE`는 시스템 날짜의 날짜만 확인하는 함수이다.
- `INTERVAL 1 DAY`는 `1일 후`라는 의미의 기간형 상수이다.
- 기간형 상수의 기술방법은 데이터베이스마다 조금씩 다르며 세세한 부분까지 표준화가 이루어지지는 않았다. 따라서 데이터베이스의 메뉴얼을 참고해야 한다.

### 날짜형 간의 뺄셈
- 날짜시간형 데이터 간에 뺄셈을 할 수 있다(덧셈도 할 수 있지만 별 의미가 없다).
- 예를 들면 `Oracle`에서는 `2014-02-28 - 2014-01-01`이라고 한다면 두 날짜 사이에 차이가 얼마나 발생하는지 계산할 수 있다.
- 한편 `MySQL`에서는 `DATEDIFF('2014-02-28', '2014-01-01')`로 계산할 수 있다.

---