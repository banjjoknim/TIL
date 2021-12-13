# 22강. 그룹화 - GROUP BY
`GROUP BY` 구를 사용해 그룹화하는 방법에 대해서 알아본다.

**`GROUP BY`**
```
SELECT * FROM 테이블명 GROUP BY 열1, 열2 ...
```

- `COUNT`의 인수로는 집합을 지정하는데, 테이블 전체 혹은 `WHERE` 구로 검색한 행이 그 대상이된다.
- `GROUP BY` 구를 사용해 집계함수로 넘겨줄 집합을 `그룹`으로 나눌 수 있다.

---

## 1. GROUP BY로 그룹화
- 같은 값을 가진 행끼리 묶어 그룹화한 집합을 집계함수로 넘겨줄 수 있다.
- 그룹으로 나눌 때에는 `GROUP BY` 구를 사용한다.
- 이때 `GROUP BY` 구에는 그룹화할 열을 지정하며, 복수로도 지정할 수 있다.

```
SELECT name FROM sample51 GROUP BY name;
```

- 결과는 `DISTINCT`를 지정했을 때와 같다.
- `GROUP BY` 구에 열을 지정하여 그룹화하면 `지정된 열의 값이 같은 행이 하나의 그룹으로 묶인다`
- `SELECT` 구에서 `name` 열을 지정하였으므로 그룹화된 `name` 열의 데이터가 클라이언트로 반환된다.
- 각 그룹으로 묶인 값들은 서로 동일하다. 즉, 결과적으로는 각각의 그룹 값이 반환된다.
- 따라서 `GROUP BY`를 지정해 그룹화하면 `DISTINCT`와 같이 중복을 제거하는 효과가 있다.

##### GROUP BY 구로 그룹화 할 수 있다!

- 사실 `GROUP BY`는 집계함수와 함께 사용하지 않으면 별 의미가 없다.
- `GROUP BY` 구로 그룹화된 각각의 그룹이 하나의 집합으로서 집계함수의 인수로 넘겨지기 때문이다.

**`GROUP BY 구와 집계함수를 조합`**
```
SELECT name, COUNT(name), SUM(quantity) FROM sample51 GROUP BY name;
```

- 예를 들면 각 점포의 일별 매출 데이터가 중앙 판매 관리시스템에 전송되어 점포별 매출실적을 집계해 어떤 점포가 매출이 올라가는지, 어떤 상품이 인기가 있는지 등을 분석할 때 사용한다.
- 여기에서 점포별, 상품별, 월별, 일별 등 특정 단위로 집계할 때 `GROUP BY`를 자주 사용한다.
- 매출실적을 조사하는 동시에 `SUM` 집계함수로 합계를 낼 수 있으며, `COUNT`로 건수를 집계하는 경우도 있다.

---

## 2. HAVING 구로 조건 지정
- 집계함수는 `WHERE` 구의 조건식에서는 사용할 수 없다.

```
SELECT name, COUNT(name) FROM sample51
WHERE COUNT(name) = 1 GROUP BY name;
```

- `name` 열을 그룹화하여 행 개수가 하나만 존재하는 그룹을 검색하고 싶었지만 에러가 발생하여 실행할 수 없다.
- 에러가 발생한 이유는 `GROUP BY`와 `WHERE` 구의 내부처리 순서와 관계있다.
- 즉, `WHERE` 구로 행을 검색하는 처리가 `GROUP BY`로 그룹화하는 처리보다 순서상 앞서기 때문이다.
- `SELECT` 구에서 지정한 별명을 `WHERE` 구에서 사용할 수 없었던 것과 같은 이유로, 그룹화가 필요한 집계함수는 `WHERE` 구에서 지정할 수 없다.

**`내부처리 순서`**
```
WHERE 구 -> GROUP BY 구 -> SELECT 구 -> ORDER BY 구
```

##### WHERE 구에서는 집계함수를 사용할 수 없다!

- 집계한 결과에서 조건에 맞는 값을 걸러내려면 `HAVING` 구를 사용한다.
- `SELECT` 명령에는 `HAVING` 구가 있다. `HAVING` 구를 사용하면 집계함수를 사용해서 조건식을 지정할 수 있다.
- `HAVING` 구는 `GROUP BY` 구의 뒤에 기술하며 `WHERE` 구와 동일하게 조건식을 지정할 수 있다.
- 조건식에는 그룹별로 집계된 열의 값이나 집계함수의 계산결과가 전달된다고 생각하면 이해하기 쉽다.
- 이때 조건식이 참인 그룹값만 클라이언트에 반환된다.
- 결과적으로 `WHERE` 구와 `HAVING` 구에 지정된 조건으로 검색하는 2단 구조가 된다.

```
1. WHERE로 검색
2. 검색한 뒤 그룹화
3. HAVING으로 검색
```

**`HAVING 구로 걸러내기`**
```
SELECT name, COUNT(name) FROM sample51 GROUP BY name HAVING COUNT(name) = 1;
```

##### 집계함수를 사용할 경우 HAVING 구로 검색조건을 지정한다!

- 그룹화보다도 나중에 처리되는 `ORDER BY` 구에서는 문제없이 집계함수를 사용할 수 있다.
- 즉, `ORDER BY COUNT(name)`과 같이 지정할 수 있다.
- `HAVING` 구는 `GROUP BY` 구 다음으로 처리된다.

**`내부처리 순서`**
```
WHERE 구 -> GROUP BY 구 -> HAVING 구 -> SELECT 구 -> ORDER BY 구
```

- 다만, `SELECT` 구보다도 먼저 처리되므로 별명을 사용할 수는 없다.
- 예를 들어 `COUNT(name)`에 `cn`이라는 별명을 붙이면, `ORDER BY` 구에서는 사용할 수 있지만 `GROUP BY` 구나 `HAVING` 구에서는 사용할 수 없다. 즉, 다음과 같은 명령은 실행할 수 없다.

```
SELECT name AS n, COUNT(name) AS cn
FROM sample51 GROUP BY n HAVING cn = 1;
```

- 단, `MySQL`과 같이 융통성 있게 별명을 사용할 수 있는 데이터베이스 제품도 있다.
- 실제로, 앞의 `SELECT` 명령은 `MySQL`에서는 실행 가능하지만 `Oracle`등에서는 에러가 발생한다.

---

## 3. 복수열의 그룹화
- `GROUP BY`를 사용할 때 주의할 점이 있다.
- `GROUP BY`에 지정한 열 이외의 열은 집계함수를 사용하지 않은 채 `SELECT` 구에 기술하면 안된다는 것이다.
- `GROUP BY name`으로 `name` 열을 그룹화 했을 때, 이 경우 `SELECT` 구에 `name`을 지정하는 것은 문제없지만, `no` 열이나 `quantity` 열을 `SELECT` 구에 그대로 지정하면 데이터베이스 제품에 따라 에러가 발생한다.
- `GROUP BY`로 그룹화하면 클라이언트로 반환되는 결과는 그룹당 하나의 행이 된다.
- 하지만 `name` 열 값이 A인 그룹의 `quantity` 열 값은 1과 2로 두 개이다.
- 이때 그룹마다 하나의 값만을 반환해야 하므로 어느 것을 반환하면 좋을지 몰라 에러가 발생한다.
- 이때 집계함수를 사용하면 집합은 하나의 값으로 계산되므로, 그룹마다 하나의 행을 출력할 수 있다.
- 즉 다음과 같이 쿼리를 작성하면 문제없이 실행할 수 있다.

```
SELECT MIN(no), name, SUM(quantity) FROM sample51 GROUP BY name;
```

##### GROUP BY에서 지정한 열 이외의 열은 집계함수를 사용하지 않은 채 SELECT 구에 지정할 수 없다!

- 만약 `no`와 `quantity`로 그룹화한다면 `GROUP BY no, quantity`로 지정한다.
- 이처럼 `GROUP BY`에서 지정한 열이라면 `SELECT` 구에 그대로 지정해도 된다.

```
SELECT name, quantity FROM sample51 GROUP BY name, quantity;
```

---

## 4. 결괏값 정렬
- `GROUP BY`로 그룹화해도 실행결과 순서롤 정렬할 수는 없다.
- 데이터베이스 내부 처리에서 같은 값을 그룹으로 나누는 과정에서 순서가 서로 바뀌는 부작용이 일어날 수도 있다.
- 하지만 이는 데이터베이스 내부처리의 문제로 데이터베이스 제품에 따라 다르다.
- 확실한 것은 `GROUP BY` 지정을 해도 정렬되지는 않는다는 점이다.
- 이럴 때는 `ORDER BY` 구를 사용해 결과를 정렬할 수 있다.
- `GROUP BY` 구로 그룹화한 경우도 `ORDER BY` 구를 사용해 정렬할 수 있다.
- 결괏값을 순서대로 정렬해야 한다면 `ORDER BY` 구를 지정해주면 된다.

**`name 열로 그룹화해 합계를 구하고 내림차순으로 정렬`**
```
SELECT name, COUNT(name), SUM(quantity)
FROM sample51 GROUP BY name ORDER BY SUM(quantity) DESC;
```

---
