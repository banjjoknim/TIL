# 빌드하고 실행하기
- 빌드해서 실행할 수 있는 파일을 만들어본다.
- 사실 인텔리제이에서만 해도 되지만, 실제 개발을 처음 하는 분들은 서버에서 빌드할 때 정말 어떻게 해야하는지를 잘 모르는 경우가 많다(서버에서는 cmd만 사용할 수 있다).
- 실무에서 개발을 하려면 cmd만으로 빌드를 해야할 때도 많다.

## Mac 사용자일 경우
- 콘솔로 이동
- `./gradlew build`
- `cd build/libs`
- `java -jar hello-spring-0.0.1-SNAPSHOT.jar`
- 실행 확인

## 윈도우 사용자일 경우
- 콘솔로 이동 대신 명령 프롬프트(cmd)로 이동.
- `./gradlew` 대신 `gradlew.bat`를 실행.
    - 명령 프롬프트에서 `gradlew.bat`를 실행하려면 `gradlew`하고 엔터를 치면 된다.
- `gradlew build`
    - 폴더 목록 확인 : `ls` 대신 `dir` 사용.
- `cd build/libs`
- `java -jar hello-spring-0.0.1-SNAPSHOT.jar`
- 실행 확인

[윈도우에서 Git Bash 터미널 사용하기](https://www.inflearn.com/questions/53961)

서버에 배포할때는 `build`를 통해 만들어진 `jar` 파일만 서버에 넣어준 다음 `java -jar`로 실행시키면 서버에서도 스프링이 동작하게 된다.

- `./gradlew clean`을 이용하면, `build` 폴더 자체가 제거된다.
- `./gradlew clean build`를 이용하면 `build` 폴더를 완전히 지우고 다시 빌드한다.
- 서버를 종료하려면 `Ctrl + C` 를 입력한다.

---
