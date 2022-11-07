# SOLID & Design Pattern Sample

## SOLID

- SOLID 원칙들은 소프트웨어 작업에서 프로그래머가 소스 코드가 읽기 쉽고 확장하기 쉽게 될 때까지 소프트웨어 소스 코드를 리팩터링하여 코드 냄새를 제거하기 위해 적용할 수 있는 지침이다.
- 각 항목별 before & after 를 비교하면서 어떤 차이가 있는지 생각해본다.
- before & after 에 새로운, 또는 동일한 규칙(비즈니스 로직, 유효성 검사 등)을 적용해야 한다고 가정하고 변경을 시도해본다.

### SRP(Single Responsibility Principle)

- 단일 책임 원칙
- 한 클래스는 하나의 책임만 가져야 한다. 즉, 한 클래스가 변경되는 이유는 한 가지여야 한다.

#### 구현 요구사항

- 예금주의 이름으로 계좌를 생성할 수 있다.
- 계좌에 입금할 수 있다.
- 계좌에서 출금할 수 있다.

## 참고자료

- [SOLID (객체 지향 설계)](https://ko.wikipedia.org/wiki/SOLID_(%EA%B0%9D%EC%B2%B4_%EC%A7%80%ED%96%A5_%EC%84%A4%EA%B3%84)) 


