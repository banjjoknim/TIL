# 회원 서비스 테스트

---

## 테스트 클래스 작성
- `Ctrl + Shift + T`
- 테스트 코드는 과감하게 한글을 사용해도 된다(영어권 사람들과 일하는 것이 아니라면).
- 빌드될 때 테스트 코드는 실제 코드에 포함되지 않는다.
- `given`, `when`, `then` 주석을 권장하는 편이다.
    - `given` : 무언가 상황이 주어졌을 때
    - `when` : 이것을 실행하면
    - `then` : 이러한 결과가 나와야 한다.
- 테스트는 예외상황이 정상상황보다 더 중요하다.
- `Ctrl + R` : 이전에 실행했던 것을 그대로 실행해준다(Mac).
    - 윈도우는 `Shift + F10`을 사용한다.

---

## Dependency Injection(의존성 주입)
- 직접 인스턴스를 만드는 것이 아니라 외부에서 넣어주는 것을 말한다.
    - `MemberSerivce` 입장에서는 직접 `new`로 인스턴스를 만드는 것이 아니라 외부에서 `MemberRepository`를 넣어주는 것이다.
    
---