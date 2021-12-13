# 21강. COUNT 이외의 집계함수
`SUM` 집계함수를 사용해 집합의 합계치를 구할 수 있다.

**`SUM, AVG, MIN, MAX`**
```
SUM ([ALL|DISTINCT] 집합)
AVG ([ALL|DISTINCT] 집합)
MIN ([ALL|DISTINCT] 집합)
MAX ([ALL|DISTINCT] 집합)
```

- SQL에서는 `SUM` 함수를 사용해 합계를 구할 수 있습니다.
- 또한 집합에서 최솟값, 최댓값을 찾는 경우에도 집계함수를 사용해 처리할 수 있습니다.

---

## 1. SUM으로 합계 구하기
- 집계함수는 `COUNT`만 있는 것이 아니다.
- `SUM 집계함수`를 사용해 집합의 합계를 구할 수 있다.
- 예를 들어 1, 2, 3이라는 세 개의 값을 가지는 집합이 있다고 한다면, `SUM` 집계함수의 인수로 이 집합을 지정하면 1+2+3으로 계산하여 6이라는 값을 반환한다.

**`SUM으로 quantity열의 합계 구하기`**
```
SELECT SUM(quantity) FROM sample51;
```

- `SUM` 집계함수에 지정되는 집합은 수치형 뿐이다.
- 문자열형이나 날짜시간형의 집합에서 합계를 구할 수는 없다.
- `name` 열은 문자열형이므로 `SUM(name)`과 같이 지정할 수는 없습니다.
- 한편, `SUM` 집계함수도 `COUNT`와 마찬가지로 `NULL` 값을 무시하며, `NULL` 값을 제거한 뒤 합계를 낸다.

##### 집계함수로 집합의 합계를 구할 수 있다!

---

## 2. AVG로 평균내기
- `SUM` 집계함수를 사용하여 집합의 합계를 구할 수 있다.
- 이때 합한 값을 개수로 나누면 `평균값`을 구할 수 있다.
- 집계함수가 반환한 값을 연산할 수도 있는데 `SUM(quantity) / COUNT(quantity)`와 같이 지정하면 된다.

**`AVG 평균값 구하기`**
```
SELECT AVG(quantity), SUM(quantity)/COUNT(quantity) FROM sample51;
```

##### AVG 집계함수로 집합의 평균값을 구할 수 있다!

- `AVG` 집계함수도 `NULL` 값은 무시한다.
- 즉, `NULL` 값을 제거한 뒤에 평균값을 계산한다.
- 만약 `NULL`을 0으로 간주해서 평균을 내고 싶다면 `CASE`를 사용해 `NULL`을 0으로 변환한 뒤에 `AVG` 함수로 계산하면 된다.

```
SELECT AVG(CASE WHEN quantity IS NULL THEN 0 ELSE quantity END) AS avgnull0 FROM sample51;
```

---

## 3. MIN - MAX로 최솟값, 최댓값 구하기
- `MIN 집계함수`, `MAX 집계함수`를 사용해 집합에서 최솟값과 최댓값을 구할 수 있다.
- 이들 함수는 문자열형과 날짜시간형에도 사용할 수 있다.
- 다만 `NULL` 값을 무시하는 기본규칙은 다른 집계함수와 같다.

**`MIN, MAX로 최솟값, 최댓값 구하기`**
```
SELECT MIN(quantity), MAX(quantity), MIN(name), MAX(name) FROM sample51;
```

---