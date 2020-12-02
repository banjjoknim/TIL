# Chapter8. EC2 서버에 프로젝트를 배포해 보자
이제 실제로 서버에 서비스를 한번 배포해 보겠습니다.

---

## 8.1 EC2에 프로젝트 Clone 받기
먼저 깃허브에서 코드를 받아올 수 있게 `EC2`에 깃을 설치합니다. `EC2`로 접속해서 다음과 같이 명령어를 입력합니다.

>sudo yum install git

설치가 완료되면 다음 명령어로 설치 상태를 확인합니다.

>git --version

깃이 성공적으로 설치되면 `git clone`으로 프로젝트를 저장할 디렉토리를 생성합니다.

>mkdir ~/app && mkdir ~/app/step1

생성된 디렉토리로 이동합니다.

>cd ~/app/step1

본인의 깃허브 웹페이지에서 `https` 주소를 복사한 뒤, 복사한 `https` 주소를 통해 `git clone`을 진행합니다.

>git clone `복사한 주소`

그러면 클론이 진행되는 것을 볼 수 있습니다.

`git clone`이 끝났으면 클론된 프로젝트로 이동해서 파일들이 잘 복사되었는지 확인합니다.

>cd `프로젝트명`
>ll(영어 LL의 소문자 - 현재 디렉토리 내의 파일 리스트를 보여준다)

프로젝트의 코드들이 모두 있으면 됩니다. 그리고 코드들이 잘 수행되는지 테스트로 검증하겠습니다.

>./gradlew test

`Chapter5`의 **기존 테스트에 Security 적용하기**까지 잘 적용했다면 정상적으로 테스트를 통과합니다.

테스트가 실패해서 수정하고 깃허브에 푸시를 했다면 프로젝트 폴더안에서 다음 명령어를 사용하면 됩니다.

>git pull

만약 다음과 같이 `gradlew` 실행 권한이 없다는 메시지가 뜬다면

>-bash: ./gradlew: Permission denied

다음 명령어로 실행 권한을 추가한 뒤 다시 테스트를 수행하면 됩니다.

>chmod +x ./gradlew

깃을 통해 프로젝트의 클론과 풀까지 잘 진행했으니 이제 프로젝트의 테스트, 빌드, 실행까지 진행합니다.

>현재 EC2엔 그레이들(Gradle)을 설치하지 않았습니다. 하지만, Gradle Task(ex: test)를 수행할 수 있습니다. 이는 프로젝트 내부에 포함된 `gradlew` 파일 때문입니다. 그레이들이 설치되지 않은 환경 혹은 버전이 다른 상황에서도 해당 프로젝트에 한해서 그레이들을 쓸 수 있도록 지원하는 `Wrapper` 파일입니다. 해당 파일을 직접 이용하기 때문에 별도로 설치할 필요가 없습니다.

> ###### 학습중 발생 오류 추가 
>Could not find or load main class org.gradle.wrapper.GradleWrapperMain 에러가 발생한다면?
>
>- 원인 : 현재 프로젝트에 `gradle/wrapper/gradle-wrapper.jar`이 존재하지 않아서 그런 것이다.
>- 참고 링크 : [Could not find or load main class org.gradle.wrapper.GradleWrapperMain](https://stackoverflow.com/questions/29805622/could-not-find-or-load-main-class-org-gradle-wrapper-gradlewrappermain)
>
>나같은 경우는 `.gitignore`에 `gradle` 디렉토리까지 포함해서 발생한 문제였다. 그래서 `.gitignore`에 `!gradle/**`을 추가해서 해결했다.

> ###### 학습중 발생 오류 추가
> Starting a Gradle Daemon (subsequent builds will be faster)
> Task :compileJava FAILED
>
>FAILURE: Build failed with an exception.
>
>* What went wrong:
>Execution failed for task ':compileJava'.
> Could not target platform: 'Java SE 11' using tool chain: 'JDK 8 (1.8)'.
>
>즉, EC2의 자바 버전과 프로젝트의 자바 버전이 달라서 컴파일이 불가능하다는 뜻이다.
>자바 버전을 맞춰서 설치하면 된다.
>- 참고 링크 : [AWS EC2에 JDK 11 설치하기](https://pompitzz.github.io/blog/java/awsEc2InstallJDK11.html)

> ###### 학습중 발생 오류 추가
>JVM crash log found: file:///home/ec2-user/app/step1/TIL/WebServiceBySpringBootAndAWS/hs_err_pid1785.log
>
>FAILURE: Build failed with an exception.
>
> Gradle build daemon disappeared unexpectedly (it may have been killed or may have crashed)
>
>Native memory allocation (mmap) failed to map 262144
>INFO: os::commit_memory(0x00007f1000196000, 262144, 0) failed; error='Not enough space' (errno=12)
>라는 문구를 발견했다. 메모리가 부족해서 생긴 문제인듯한데.. 어떻게 해결해야 할지 모르겠다...

---

## 8.2 배포 스크립트 만들기
작성한 코드를 실제 서버에 반영하는 것을 배포라고 합니다. 이 책에서 배포라 하면 다음의 과정을 모두 포괄하는 의미라고 보면 됩니다.

- `git clone` 혹은 `git pull`을 통해 새 버전의 프로젝트 받음
- `Gradle`이나 `Maven`을 통해 프로젝트 테스트와 빌드
- `EC2` 서버에서 해당 프로젝트 실행 및 재실행

앞선 과정을 **배포할 때마다 개발자가 하나하나 명령어를 실행**하는 것은 불편함이 많습니다. 그래서 이를 쉘 스크립트로 작성해 스크립트만 실행하면 앞의 과정이 차례로 진행되도록 하겠습니다. 참고로 쉘 스크립트와 빔(`vim`)은 서로 다른 역할을 합니다. 쉘 스크립트는 `.sh`라는 파일 확장자를 가진 파일입니다. `node.js`가 `.js`라는 파일을 통해 서버에서 작동하는 것처럼 쉘 스크립트 역시 리눅스에서 기본적으로 사용할 수 있는 스크립트 파일의 한종류입니다.

빔은 리눅스 환경과 같이 `GUI`가 아닌 환경에서 사용할 수 있는 편집 도구입니다. 리눅스에선 빔 외에도 이맥스(`Emacs`), 나노(`nano`)등의 도구를 지원하지만 가장 대중적인 도구가 빔이다보니 이 책에서도 빔으로 리눅스 환경에서의 편집을 진행하겠습니다.

`~/app/step1/`에 `deploy.sh` 파일을 하나 생성합니다.

>vim ~/app/step1/deploy.sh

참고 링크 : [초심자를 위한 최소한의 vim 가이드](http://bit.ly/2Q3BpvZ)

다음의 코드를 추가합니다.


>#!/bin/bash
>
>REPOSITORY=/home/ec2-user/app/step1 // 1.
>PROJECT_NAME=springboot2-webservicee
>
>cd \$REPOSITORY/$PROJECT_NAME/ // 2.
>
>echo "> Git Pull" // 3.
>
>./gradlew build // 4.
>
>echo "> step1 디렉토리로 이동"
>
>cd $REPOSITORY
>
>echo "> Build 파일 복사"
>
>cp \$REPOSITORY/\$PROJECT_NAME/build/libs/*.jar $REPOSITORY/ // 5.
>
> echo "> 현재 구동중인 애플리케이션 pid 확인"
>
>CURRENT_PID=\$(pgrep -f ${PROJECT_NAME}.*.jar) // 6.
>
>echo "현재 구동 중인 애플리케이션 pid: \$CURRENT_PID"
>
>if [ -z "$CURRENT_PID" ]; then // 7.
>   echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
>else 
>   echo "> kill -15 \$CURRENT_PID"
>   kill -15 \$CURRENT_PID
>   sleep 5
>fi
>
>echo "> 새 애플리케이션 배포"
>
>JAR_NAME=\$(ls -tr $REPOSITORY/ | grep jar | tail -n 1) // 8.
>
>echo "> JAR Name : $JAR_NAME"
>
>nohup java -jar \$REPOSITORY/$JAR_NAME 2>&1 & // 9.

#### 코드설명

**1. REPOSITORY=/home/ec2-user/app/step1**
- 프로젝트 디렉토리 주소는 스크립트 내에서 자주 사용하는 값이기 때문에 이를 변수로 저장합니다.
- 마찬가지로 `PROJECT_NAME=springboot2-webservicee`도 동일하게 변수로 저장합니다.
- 쉘에서는 **타입 없이** 선언하여 저장합니다.
- 쉘에서는 `$ 변수명`으로 변수를 사용할 수 있습니다.

**2. cd \$REPOSITORY/$PROJECT_NAME/**
- 제일 처음 `git clone` 받았던 디렉토리로 이동합니다.
- 바로 위의 쉘 변수 설명을 따라 `/home/ec2-user/app/step1/springboot2-webservicee` 주소로 이동합니다.

**3. git pull**
- 디렉토리 이동 후, `master` 브랜치의 최신 내용을 받습니다.

**4. ./gradlew build**
- 프로젝트 내부의 `gradlew`로 `build`를 수행합니다.

**5. cp \$REPOSITORY/\$PROJECT_NAME/build/libs/*.jar $REPOSITORY/**
- `build`의 결과물인 `jar` 파일을 복사해 `jar` 파일을 모아둔 위치로 복사합니다.

**6. CURRENT_PID=\$(pgrep -f ${PROJECT_NAME}.*.jar)**
- 기존에 수행 중이던 스프링 부트 애플리케이션을 종료합니다.
- `pgrep`은 `process id`만 추출하는 명령어입니다.
- `-f` 옵션은 프로세스 이름으로 찾습니다.

**7. if ~ else ~ fi**
- 현재 구동 중인 프로세스가 있는지 없는지를 판단해서 기능을 수행합니다.
- `process id` 값을 보고 프로세스가 있으면 해당 프로세스를 종료합니다.

**8. JAR_NAME=\$(ls -tr $REPOSITORY/ | grep jar | tail -n 1)**
- 새로 실행할 `jar` 파일명을 찾습니다.
- 여러 `jar` 파일이 생기기 때문에 `tail -n`로 가장 나중의 `jar` 파일(최신 파일)을 변수에 저장합니다.

**9. nohup java -jar \$REPOSITORY/$JAR_NAME 2>&1 &**
- 찾은 `jar` 파일명으로 해당 `jar` 파일을 `nohup`으로 실행합니다.
- 스프링 부트의 장점으로 특별히 외장 톰캣을 설치할 필요가 없습니다.
- 내장 톰캣을 사용해서 `jar` 파일만 있으면 바로 웹 애플리케이션 서버를 실행할 수 있습니다.
- 일반적으로 자바를 실행할 때는 `java -jar`라는 명령어를 사용하지만, 이렇게 하면 사용자가 터미널 접속을 끊을 때 애플리케이션도 같이 종료됩니다.
- 애플리케이션 실행자가 터미널을 종료해도 애플리케이션은 계속 구동될 수 있도록 `nohup` 명령어를 사용합니다.

이렇게 생성한 스크립트에 실행 권한을 추가합니다.

>chmod +x ./deploy.sh

그리고 다시 확인해 보면 `x` 권한이 추가된 것을 확인할 수 있습니다.

![Chapter8_deploy](https://user-images.githubusercontent.com/68052095/100843245-6d2c1180-34bd-11eb-834d-5e6f9de1185e.PNG)

이제 이 스크립트를 다음 명령어로 실행합니다.

>./deploy.sh

그러면 다음과 같이 로그가 출력되며 애플리케이션이 실행됩니다.

![Chapter8_deploy_seccuess](https://user-images.githubusercontent.com/68052095/100846838-59cf7500-34c2-11eb-9915-e48913c45aa0.PNG)

>###### 학습중 오류 발생 추가
>
>![Chapter8_deploy_fail1](https://user-images.githubusercontent.com/68052095/100846844-5b00a200-34c2-11eb-9500-0a86854c322a.PNG)
>
>위 사진와 같은 오류가 발생해서 디렉토리 계층을 전부 파악해야 했다.
>
>![Chapter8_deploy_fail2](https://user-images.githubusercontent.com/68052095/100846842-5a680b80-34c2-11eb-9099-c14e8a8dbc0c.PNG)
>
>내 경우에는 디렉토리 구조가 
/home/ec2-user/app/step1/TIL/WebServiceBySpringBootAndAWS 였다. 
따라서 이에 맞게 `deploy.sh`의 변수 값을 수정해서 해결했다.
>
>![Chapter8_deploy_fail3](https://user-images.githubusercontent.com/68052095/100847170-ce0a1880-34c2-11eb-97e3-1dc406ed0dfa.PNG)

잘 실행되었으니 `nohup.out` 파일을 열어 로그를 보겠습니다. `nohup.out`은 실행되는 애플리케이션에서 출력되는 모든 내용을 갖고 있습니다.

> vim nohup.out

`nohup.out` 제일 아래로 가면 `ClientRegistrationRepository`를 찾을 수 없다(`that could not be found.`)는 에러가 발생하면서 애플리케이션 실행에 실패했다는 것을 알 수 있습니다.

>오류 로그
>
>Method springSecurityFilterChain in org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration required a bean of type 'org.springframework.security.oauth2.client.registration.ClientRegistrationRepository' that could not be found.


왜 이렇게 되었을까요?

---

## 8.3 외부 Security 파일 등록하기
이유는 다음과 같습니다. `ClientRegistrationRepository`를 생성하려면 `clientId`와 `clientSecret`가 필수입니다. 로컬 PC에서 실행할 때는 `application-oauth.properties`가 있어서 문제가 없었습니다.

하지만 이 파일은 **.gitignore로 git에서 제외 대상**이라 깃허브에는 올라가있지 않습니다. 애플리케이션을 실행하기 위해 공개된 저장소에 `ClinetId`와 `ClientSecret`을 올릴 수는 없으니 **서버에서 직접 이 설정들을 가지고 있게** 하겠습니다.

먼저 `step1`이 아닌 `app` 디렉토리에 `properties` 파일을 생성합니다.

>vim /home/ec2-user/app/application-oauth.properties

그리고 로컬에 있는 `application-oauth.properties` 파일 내용을 그대로 붙여넣기를 한 뒤, 해당 파일을 저장하고 종료합니다(`:wq`). 그리고 방금 생성한 `application-oauth.properties`을 쓰도록 `deploy.sh` 파일을 수정합니다.

**`deploy.sh`**
>...
>nohup java -jar \ -Dspring.config.location=classpath:/application.properties,/home/ec2-user/app/application-oauth.properties \ \$REPOSITORY/$JAR_NAME 2>&1 &

##### 코드 설명
**1. -Dspring.config.location**
- 스프링 설정 파일 위치를 지정합니다.
- 기본 옵션들을 담고 있는 `application-properties`과 `OAuth` 설정들을 담고 있는 `application-oauth.properties`의 위치를 지정합니다.
- `classpath`가 붙으면 `jar` 안에 있는 `resources` 디렉토리를 기준으로 경로가 생성됩니다.
- `application-oauth.properties`은 절대경로를 사용합니다. 외부에 파일이 있기 때문입니다.

수정이 다 되었다면 다시 `deploy.sh`를 실행해 봅니다.

>###### 학습중 발생 오류 추가
>Error: Unable to access jarfile  -Dspring.config.location=classpath:/application.properties,/home/ec2-user/app/application-oauth.properties
Error: Unable to access jarfile
>
>띄어쓰기가 잘못이었다!!!!!!! `\`의 의미는 `이어쓰기`라고 한다...

그럼 다음과 같이 정상적으로 실행된 것을 확인할 수 있습니다.

![Chapter8_deploy_seccuess2](https://user-images.githubusercontent.com/68052095/100858992-09f8aa00-34d2-11eb-9d08-bfb358d3d21e.PNG)

마지막으로 `RDS`에 접근하는 설정도 추가해 보겠습니다.

---

## 8.4 스프링 부트 프로젝트로 RDS 접근하기
`RDS`는 `MariaDB`를 사용 중입니다. 이 `MariaDB`에서 스프링부트 프로젝트를 실행하기 위해선 몇 가지 작업이 필요합니다. 진행할 작업은 다음과 같습니다.

- **테이블 생성** : `H2`에서 자동 생성해주던 테이블들을 `MariaDB`에선 직접 쿼리를 이용해 생성합니다.
- **프로젝트 생성** : 자바 프로젝트가 `MariaDB`에 접근하려면 데이터베이스 드라이버가 필요합니다. `MariaDB`에서 사용 가능한 드라이버를 프로젝트에 추가합니다.
- **EC2 (리눅스 서버) 설정** : 데이터베이스의 접속 정보는 중요하게 보호해야 할 정보입니다. 공개되면 외부에서 데이터를 모두 가져갈 수 있기 때문입니다. 프로젝트 안에 접속 정보를 갖고 있다면 깃허브와 같이 오픈된 공간에선 누구나 해킹할 위험이 있습니다. `EC2` 서버 내부에서 접속 정보를 관리하도록 설정합니다.

### RDS 테이블 생성
먼저 `RDS` 테이블을 생성합니다. 여기선 `JPA`가 사용될 엔티티 테이블과 스프링 세션이 사용될 테이블 2가지 종류를 생성합니다. `JPA`가 사용할 테이블은 **테스트 코드 수행 시 로그로 생성되는 쿼리를 사용하면 됩니다.** 테스트 코드를 수행하면 다음과 같이 로그가 발생하니 `create table`부터 복사하여 `RDS`에 반영합니다.

>Hibernate: create table posts (id bigint not null auto_increment, created_date datetime, modified_date datetime, author varchar(255), content TEXT not null, title varchar(500) not null, primary key (id)) engine=InnoDB
Hibernate: create table user (id bigint not null auto_increment, created_date datetime, modified_date datetime, email varchar(255) not null, name varchar(255) not null, picture varchar(255), role varchar(255) not null, primary key (id)) engine=InnoDB

스프링 세션 테이블은 `schema-mysql.sql` 파일에서 확인할 수 있습니다. `File` 검색으로 찾습니다.

>CREATE TABLE SPRING_SESSION(
    PRIMARY_ID            CHAR(36) NOT NULL,
    SESSION_ID            CHAR(36) NOT NULL,
    CREATION_TIME         BIGINT   NOT NULL,
    LAST_ACCESS_TIME      BIGINT   NOT NULL,
    MAX_INACTIVE_INTERVAL INT      NOT NULL,
    EXPIRY_TIME           BIGINT   NOT NULL,
    PRINCIPAL_NAME        VARCHAR(100),
    CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)
) ENGINE = InnoDB ROW_FORMAT = DYNAMIC;
>
>CREATE UNIQUE INDEX SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID);
CREATE INDEX SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME);
>
>CREATE TABLE SPRING_SESSION_ATTRIBUTES(
    SESSION_PRIMARY_ID CHAR(36)     NOT NULL,
    ATTRIBUTE_NAME     VARCHAR(200) NOT NULL,
    ATTRIBUTE_BYTES    BLOB         NOT NULL,
    CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
    CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION (PRIMARY_ID) ON DELETE CASCADE
) ENGINE = InnoDB ROW_FORMAT = DYNAMIC;

이것 역시 복사하여 `RDS`에 반영합니다. `RDS`에 필요한 테이블은 모두 생성하였으니 프로젝트 설정으로 넘어갑니다.

### 프로젝트 설정
먼저 `MariaDB` 드라이버를 `build.gradle`에 등록합니다(현재는 `H2` 드라이버만 있는 상태).

>compile('org.mariadb.jdbc:mariadb-java-client')

그리고 서버에서 구동될 환경을 하나 구성합니다(여기서 환경이란 스프링의 `profile`을 이야기합니다).
`src/main/resources/`에 `application-real.properties` 파일을 추가합니다. 앞에서 이야기한 대로 `application-real.properties` 로 파일을 만들면 `profile=real`인 환경이 구성된다고 보면 됩니다. 실제 운영될 환경이기 때문에 보안/로그상 이슈가 될 만한 설정들을 모두 제거하며 **RDS 환경 profile** 설정이 추가됩니다.

>spring.profiles.include=oauth,real-db
>spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
>spring.session.store-type=jdbc

모든 설정이 완료되었다면 깃허브로 푸시합니다.

### EC2 설정
`OAuth`와 마찬가지로 `RDS` 접속 정보도 보호해야 할 정보이니 `EC2` 서버에 직접 설정 파일을 둡니다.
`app` 디렉토리에 `application-real-db.properties` 파일을 생성합니다.

>vim ~/app/application-real-db.properties

그리고 다음과 같은 내용을 추가합니다.

>spring.jpa.hibernate.ddl-auto=none
>spring.datasource.url=jdbc:mariadb://rds주소:포트명(기본은 3306)/database이름
>spring.datasource.username=db계정
>spring.datasource.password=db계정 비밀번호
>spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

##### 코드설명
**1. spring.jpa.hibernate.ddl-auto=none**
- `JPA`로 테이블이 자동 생성되는 옵션을 `None`(생성하지 않음)으로 지정합니다.
- `RDS`에는 실제 운영으로 사용될 테이블이니 절대 스프링 부트에서 새로 만들지 않도록 해야 합니다.
- 이 옵션을 하지 않으면 자칫 테이블이 모두 새로 생성될 수 있습니다.
- 주의해야 하는 옵션입니다.

마지막으로 `deploy.sh`가 `real profile`을 쓸 수 있도록 다음과 같이 개선합니다.

>...
nohup java -jar \
        -Dspring.config.location=classpath:/application.properties,/home/ec2-user/app/application-oauth.properties,/home/ec2-user/app/application-real-db.properties,classpath:/application-real.properties \
        -Dspring.profiles.active-real \
        \$REPOSITORY/$JAR_NAME 2>&1 &

##### 코드설명
**1. -Dspring.profiles.active=real**
- `application-real.properties`를 활성화시킵니다.
- `application-real.properties`의 `spring.profiles.include=oauth,real-db` 옵션 때문에 `real-db` 역시 함께 활성화 대상에 포함됩니다.

이렇게 설정한 후 다시 `deploy.sh`를 실행해 봅니다. `nohup.out` 파일을 열어 다음과 같이 로그가 보인다면 성공적으로 수행된 것입니다.

> Tomcat started on port(s): 8080 (http) with context path ''
Started Application in 13.29 seconds (JVM running for ~~~)

`curl` 명령어로 `html` 코드가 정상적으로 보인다면 성공입니다.

>curl localhost:8080

마지막으로 실제 브라우저에서 로그인을 시도해 보겠습니다.

---

## 8.5 EC2에서 소셜 로그인하기
`curl` 명령어를 통해 `EC2`에 서비스가 잘 배포된 것을 확인하였으니 이제 브라우저에서 확인해볼 텐데, 그 전에 다음과 같은 몇 가지 작업을 해보겠습니다.

#### AWS 보안 그룹 변경
먼저 `EC2`에 스프링 부트 프로젝트가 8080 포트로 배포되었으니, 8080포트가 보안 그룹에 열려 있는지 확인한 뒤, 해당 그룹의 인바운드 규칙에 8080 포트가 열려있다면 OK, 안 되어있다면 추가해줍니다.

#### AWS EC2 도메인으로 접속
왼쪽 사이드바의 `[인스턴스]` 메뉴를 클릭해서 본인이 생성한 `EC2` 인스턴스를 선택하면 다음과 같이 상세 정보에서 **퍼블릭 DNS**를 확인할 수 있습니다.

이 주소가 `EC2`에 자동으로 할당된 **도메인**입니다. 인터넷이 되는 장소 어디나 이 주소를 입력하면 우리의 `EC2` 서버에 접근할 수 있습니다. 이 도메인 주소에 8080 포트를 붙여 브라우저에 입력하면 확인할 수 있습니다.

하지만 현재 상태에서는 해당 서비스에 **EC2의 도메인을 등록하지 않았기 때문에** 구글과 네이버 로그인이 작동하지 않습니다.

먼저 구글에 등록합니다.

#### 구글에 EC2 주소 등록
[구글 웹 콘솔](https://console.cloud.google.com/home/dashboard)로 접속하여 본인의 프로젝트로 이동한 다음 `[API 및 서비스 -> 사용자 인증 정보]`로 이동합니다.

해당하는 `OAuth 2.0 클라이언트 ID`를 선택한 뒤, **승인된 리디렉션 URI**에 `EC2`의 퍼블릭 `DNS`를 등록합니다. 그리고 퍼블릭 `DNS` 주소에 `:8080/login/oauth2/code/google` 주소를 추가하여 승인된 리디렉션 URI에 등록합니다.

이제 `EC2 DNS` 주소로 이동해서 다시 구글 로그인을 시도해 보면 같이 로그인이 정상적으로 수행되는 것을 확인할 수 있습니다.

![Chapter8_I_got_it!!!!](https://user-images.githubusercontent.com/68052095/100868071-6f529800-34de-11eb-86b4-92c54007e9f5.PNG)

### ~~해냈다!!!!!!!~~

#### 네이버에 EC2 주소 등록
[네이버 개발자 센터](https://developers.naver.com/apps/#/myapps)로 접속해서 본인의 프로젝트로 이동합니다.

메뉴중에서 API설정 탭에 들어간 뒤, 아래로 내려가 보면 PC 웹 항목이 있는데 여기서 **서비스 URL과 Callback URL** 2개를 수정합니다.

(1) 서비스 URL
- 로그인을 시도하는 서비스가 네이버에 등록된 서비스인지 판단하기 위한 항목입니다.
- 8080 포트는 제외하고 실제 도메인 주소만 입력합니다.
- 네이버에서 아직 지원되지 않아 하나만 등록 가능합니다.
- 즉, `EC2`의 주소를 등록하면 `localhost`가 안됩니다.
- 개발 단계에서는 등록하지 않는 것을 추천합니다.
- `localhost`도 테스트하고 싶으면 네이버 서비스를 하나 더 생성해서 키를 발급받으면 됩니다.

(2) Callback URL
- 전체 주소를 등록합니다(`EC2 퍼블릭 DNS:8080/login/oauth2/code/naver`)

2개 항목을 모두 수정/추가하였다면 구글과 마찬가지로 네이버 로그인도 정상적으로 수행되는 것을 확인할 수 있습니다.

간단하게나마 스프링 부트 프로젝트를 `EC2`에 배포해 보았습니다. 스크립트를 작성해서 간편하게 빌드와 배포를 진행한 것 같지만 현재 방식은 몇 가지 문제가 있습니다.

- 수동 실행되는 `Test`
  - 본인이 짠 코드가 다른 개발자의 코드에 영향을 미치지 않는지 확인하기 위해 전체 테스트를 수행해야만 합니다.
  - 현재 상태에선 항상 개발자가 작업을 진행할 때마다 수동으로 전체 테스트를 수행해야만 합니다.

- 수동 `Build`
  - 다른 사람이 작성한 브랜치와 본인이 작성한 브랜치가 합쳐졌을 때(`Merge`) 이상이 없는지는 `Build`를 수행해야만 알 수 있습니다.
  - 이를 매번 개발자가 직접 실행해봐야만 합니다.

다음 작업은 이런 수동 `Test & Build`를 자동화시켜서 **깃허브에 푸시를 하면 자동으로 Test & Build & Deploy**가 진행되도록 하는 것입니다.

---