# Spring-Jooq-Liquibase

## JOOQ

### Docs.

- [The jOOQ User Manual](https://www.jooq.org/doc/3.14/manual/)
- [jOOQ as a SQL builder without code generation](https://www.jooq.org/doc/3.14/manual/getting-started/use-cases/jooq-as-a-sql-builder-without-codegeneration/)
- [jOOQ as a SQL builder with code generation](https://www.jooq.org/doc/3.14/manual/getting-started/use-cases/jooq-as-a-sql-builder-with-code-generation/)
- [Configuration and setup of the generator](https://www.jooq.org/doc/3.14/manual/code-generation/codegen-configuration/)
- [Running the code generator with Gradle](https://www.jooq.org/doc/3.14/manual/code-generation/codegen-gradle/)
- [Jdbc](https://www.jooq.org/doc/3.14/manual/code-generation/codegen-advanced/codegen-config-jdbc/)
- [KotlinGenerator](https://www.jooq.org/doc/3.14/manual/code-generation/kotlingenerator/)
- [etiennestuder/gradle-jooq-plugin](https://github.com/etiennestuder/gradle-jooq-plugin)
    - [Working with Configurations using the Gradle Kotlin DSL](https://github.com/etiennestuder/gradle-jooq-plugin/blob/main/KotlinDSL.md)

#### Trouble Shooting

- Spring Boot의 버전에 맞춰서 `MySQL Driver, jooqGenerator ...`등의 Dependency 를 추가해주어야 한다. 그렇지 않으면 `./gradlew generateJooq` 명령어
  실행시 `MySQL Driver` 를 찾지 못해서 실패한다.
    - [Spring Boot 2.7.9 - Dependency Versions](https://docs.spring.io/spring-boot/docs/2.7.9/reference/html/dependency-versions.html)
    - [Spring Boot 2.7 Release Notes#MySQL_JDBC_Driver](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.7-Release-Notes#mysql-jdbc-driver)
- `./gradlew generateJooq` 실행시 `java.lang.ClassNotFoundException: jakarta.xml.bind.annotation.XmlSchema` 발생하는 경우
    - [java.lang.ClassNotFoundException: jakarta.xml.bind.annotation.XmlSchema](https://github.com/etiennestuder/gradle-jooq-plugin/issues/209)

## Liquibase

### Docs.

- [Using Liquibase with Spring Boot](https://docs.liquibase.com/tools-integrations/springboot/springboot.html)
- [Concepts - ChangeLog](https://docs.liquibase.com/concepts/changelogs/home.html)
    - [ChangeSet](https://docs.liquibase.com/concepts/changelogs/changeset.html)
    - [Substituting Properties in Changelogs](https://docs.liquibase.com/concepts/changelogs/property-substitution.html)
    - [Example Changelogs: YAML Format](https://docs.liquibase.com/concepts/changelogs/yaml-format.html)
- [Change Types](https://docs.liquibase.com/change-types/home.html)
    - [Change Types/Miscellaneous - Using the sqlFile Change Type](https://docs.liquibase.com/change-types/sql-file.html)
    - [Change Types/Miscellaneous - include](https://docs.liquibase.com/change-types/include.html)
    - [Change Types/Miscellaneous - includeAll](https://docs.liquibase.com/change-types/includeall.html)
