# 회원 웹 기능 - 조회

---

## 템플릿 엔진
- `${키}`은 모델안에 있는 해당 키의 값을 꺼내는 것이다.
    - ex. `${members}`는 모델에서 `members`라는 키에 해당하는 값을 꺼내온다.
- `th:each="member : ${members}"` : `members` 리스트의 루프를 돌면서 객체를 하나씩 `member`에 할당한 뒤, 해당 태그 내부의 로직을 실행한다. 자바의 `for-each`문과 비슷하다.
- `${member.id}` : 위에서 할당된 `member` 객체에서 `getId`를 호출한 결과값이 할당된다.
- `${member.name}` : 위에서 할당된 `member` 객체에서 `getName`를 호출한 결과값이 할당된다.

---