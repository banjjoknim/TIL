# 회원 도메인과 리포지토리 만들기

---

## Member class
- id : 임의의 값(고객이 정하는 아이디가 아닌 데이터 구분을 위해 시스템이 저장하는 ID)

---

## Optional
- 자바8에 추가된 기능이다.
- 최근에는 `null`을 처리하는 방법 중에서 `null`을 `Optional`로 감싸서 반환하는 방법을 선호한다.

---

## HashMap, long
- 실무에서는 동시성 문제가 발생할 수 있으므로 공유되는 변수에는 `ConcurrentHashMap<>`을 사용해야 한다.
- 이 역시 동시성 문제를 고려해서 `atomicLong` 등을 사용해야 한다.

---

## Optional.ofNullable(store.get(id))
- `store.get(id)`가 `null`이어도 감싸서 반환할 수 있다.

---

## findAll()
- 실무에서는 리스트를 많이 사용한다(편리하다고 함).

---

## store.values()
- `store`라는 `Map`의 값들을 불러온다.

---