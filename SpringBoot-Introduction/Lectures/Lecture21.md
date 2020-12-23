# 스프링 JdbcTemplate
- 순수 Jdbc와 동일한 환경설정을 하면 된다.
- 스프링 JdbcTemplate과 MyBatis 같은 라이브러리는 JDBC API에서 본 반복 코드를 대부분 제거해준다. 하지만 SQL은 직접 작성해야 한다.
- 실무에서도 많이 사용한다.
- 사용하기 위해서는 `org.springframework.jdbc.core.JdbcTemplate`가 필요하다.
- `org.springframework.jdbc.core.JdbcTemplate`는 주입 받을 수 있는 것은 아니며, `DataSource`를 주입 받는다.

```java
private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTemplateMemberRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
```
- 위와 같은 형태로 사용한다.
- 이때 생성자가 단 하나만 있으면 스프링 빈으로 등록할 때 `@Autowired`를 생략할 수 있다.

```java
private RowMapper<Member> memberRowMapper() {
    return (rs, rowNum) -> {
        Member member = new Member();
        member.setId(rs.getLong("id"));
        member.setName(rs.getString("name"));
        return member;
    };
}
```

- `디자인 패턴` 중에서 `템플릿 메서드 패턴`을 사용해서 중복을 제거하고 코드를 줄인 결과가 위의 코드이다.
- `JdbcTemplate`에서 쿼리를 날려서 나온 결과를 `RowMapper`를 통해서 매핑한 뒤, 그 결과를 리스트로 받아서 `Optional`로 변환시켜서 반환한다.
- `RowMapper`는 `resultSet` 결과를 `Member` 객체로 매핑하고 생성한 다음에 반환해준다.

---