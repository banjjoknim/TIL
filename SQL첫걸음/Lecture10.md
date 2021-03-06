# 10강. 복수의 열을 지정해 정렬하기
**`ORDER BY 구`**
```
SELECT 열명 FROM 테이블명 WHERE 조건식
ORDER BY 열명1 [ASC | DESC], 열명2 [ASC | DESC]...
```

- 데이터양이 많을 경우 하나의 열만으로는 행을 특정짓기 어려운 때가 많다.
- 이런 경우 복수의 열을 지정해 정렬하면 편리하다.
- 정렬 시에는 `NULL` 값에 주의해야 한다.

---

## 1. 복수 열로 정렬 지정
- 데이터베이스 서버 당시 상황에 따라 어떤 순서로 행을 반환할지 결정된다.
- 따라서 언제나 같은 손서로 결과를 얻고 싶다면 반드시 `ORDER BY` 구로 순서를 지정해야 한다.
- `ORDER BY` 구를 지정해도 1개의 열만으로는 정확히 순서를 결정할 수 없는 경우가 많다(같은 값이 들어가 있는 경우).

### ORDER BY로 복수 열 지정하기
- `ORDER BY` 구에는 복수로 열을 지정할 수 있다.
- `SELECT` 구에서 열을 지정한 것처럼 `콤마(,)`로 열명을 구분해 지정하면 된다.

**`복수 열로 정렬하기`**
```
SELECT 열명 FROM 테이블명 ORDER BY 열명1, 열명2 ...
```

- 복수 열을 지정하면 정렬 결과가 바뀐다.
- 정렬 순서는 지정한 열명의 순서를 따른다.
- 값이 같아 순서를 결정할 수 없는 경우에는 다음으로 지정한 열명을 기준으로 정렬하는 식으로 처리된다.

##### ORDER BY 구에 복수의 열을 지정할 수 있다!

---

## 2. 정렬방법 지정하기
- 복수열을 지정한 경우에도 각 열에 대해 개별적으로 정렬방법을 지정할 수 있다.
- 이때는 각 열 뒤에 `ASC`나 `DESC`를 붙여준다.

**`복수 열 정렬`**
```
SELECT 열명 FROM 테이블명
ORDER BY 열명1 [ASC | DESC], 열명2 [ASC | DESC] ...
```

- 구문 중에 `[]` 부분은 생략할 수 있다. `|`는 둘 중 하나라는 뜻이며 `...`는 동일한 형태로 연속해서 지정할 수 있다는 의미이다.
- 이를 활용해 각 열의 정렬방법을 다르게 지정할 수 있다.
- 복수 열을 지정하는 경우에도 정렬방법을 생략하면 기본값은 `ASC`가 된다.

---

## 3. NULL 값의 정렬순서
- `NULL`에 관해서는 그 특성상 대소비교를 할 수 없어 정렬 시에는 별도의 방법으로 취급한다.
- 이때 `특정 값보다 큰 값`, `특정 값보다 작은 값`의 두 가지로 나뉘며 이 중 하나의 방법으로 대소를 비교한다.
- 간단히 말하면, `ORDER BY`로 지정한 열에서 `NULL` 값을 가지는 행은 `가장 먼저 표시`되거나 `가장 나중에 표시`된다.
- `NULL`에 대한 대소비교 방법은 표준 SQL에도 규정되어 있지 않아 데이터베이스 제품에 따라 기준이 다르다.
- `MySQL`의 경우는 `NULL` 값을 가장 작은 값으로 취급해 `ASC(오름차순)`에서는 가장 먼저, `DESC(내림차순)`에서는 가장 나중에 표시한다.

---