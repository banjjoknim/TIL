# JPA @Query(nativeQuery = true) 사용시 주의할 점

## 문제 상황

평화롭게(?) 개발을 진행하던 도중, 조금 복잡한 쿼리를 작성해야 할 일이 생겼다(querydsl로도 엄두가 안났다). 그래서 JPA Repository의 @Query(nativeQuery = true)를 사용해서
해당 기능을 구현했다. 구현을 마치고 테스트를 시도했는데, 아래와 같은 에러를 마주쳤다.

![](https://images.velog.io/images/banjjoknim/post/50bae7b1-ef63-4ef9-b7ae-37a319218c3d/image.png)

![](https://images.velog.io/images/banjjoknim/post/5a28ece2-e260-49df-9cda-4ceeef4827a2/image.png)

대충 읽어보면, `JPA-style positional param was not an integral ordinal` 이 원인이라고 한다.

## 해결

해당 예외 메시지로 검색을 해보면 `org.hibernate.engine.query.spi.ParameterParser` 를 찾을 수 있다.

![](https://images.velog.io/images/banjjoknim/post/98fad969-cae0-4926-a09b-dbbaf200bbdd/image.png)

195번 라인을 살펴보면, 동일한 예외가 보이고 있는데, 쿼리상에 `?` 라는 문자열을 기준으로 파싱을 해주고 있다(?1, ?2 와 같이 파라미터를 입력할 경우).

그런데 자세히 살펴보면 `NumberFormatException`이 발생했을 경우 해당 예외를 `catch` 해서 `QueryException`을 던지고 있다. 즉, `?1`, `?2` 와 같이 입력되는 파라미터를
쿼리에 집어넣을 경우, `?` 뒤의 숫자를 `Integer.parseInt()`로 파싱하는 것이다. 여기에 실패하면 앞서 언급했던 예외가 발생하게 되는 것이다.

![](https://images.velog.io/images/banjjoknim/post/def097cd-db91-4f3e-b813-73908d98516a/image.png)

본인의 경우에는 마지막 파라미터인 offset 부분의 `?5;` 가 문제를 일으키는 원인이었고, `세미콜론(;)`을 제거하자 정상적으로 동작했다(띄어쓰기도 가능하다).

## 결론

JPA의 Repository에서 @Query(nativeQuery = true) 를 사용할 경우, 쿼리 상에서 파라미터를 입력하는 부분에는 특히 신경써서 작성하도록 하자(~~띄어쓰기를 잘하자~~).