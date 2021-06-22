# Chapter10. 24시간 365일 중단 없는 서비스를 만들자
`Travis CI`를 활용하여 배포 자동화 환경을 구축해 보았습니다. 하지만 배포하는 동안 애플리케이션이 종료된다는 문제가 남았습니다. 긴 기간은 아니지만, **새로운 Jar가 실행되기 전까진 기존 Jar를 종료시켜 놓기 때문에** 서비스가 중단됩니다. 

---

## 10.1 무중단 배포 소개
서비스를 정지하지 않고, 배포할 수 있는 방법을 **무중단 배포**라고 합니다.

무중단 배포 방식에는 몇 가지가 있습니다.

- `AWS`에서 블루 그린(`Blue-Green`) 무중단 배포
- 도커를 이용한 웹서비스 무중단 배포

이 외에도 `L4 스위치`를 이용한 무중단 배포 방법도 있지만, `L4`가 워낙 고가의 장비이다 보니 대형 인터넷 기업 외에는 쓸 일이 거의 없습니다.

여기서 진행할 방법은 **엔진엑스(Nginx)** 를 이용한 무중단 배포입니다. 엔진엑스는 웹 서버, 리버스 프록시, 캐싱, 로드 밸런싱, 미디어 스트리밍 등을 위한 오픈소스 소프트웨어입니다.

엔진엑스가 가지고 있는 여러 기능 중 `리버스 프록시`가 있습니다. `리버스 프록시`란 엔진엑스가 **외부의 요청을 받아 백엔드 서버로 요청을 전달**하는 행위를 이야기합니다. 리버스 프록시 서버(엔진엑스)는 요청을 전달하고, 실제 요청에 대한 처리는 뒷단의 웹 애플리케이션 서버들이 처리합니다.

엔진엑스를 이용한 무중단 배포를 하는 이유는 간단합니다. **가장 저렴하고 쉽기 때문**입니다.

기존에 쓰던 `EC2`에 그대로 적용하면 되므로 배포를 위해 `AWS EC2 인스턴스`가 하나 더 필요하지 않습니다. 추가로 이 방식은 꼭 `AWS`와 같은 클라우드 인프라가 구축되어 있지 않아도 사용할 수 있는 범용적인 방법입니다. 즉, 개인 서버 혹은 사내 서버에서도 동일한 방식으로 구축할 수 있으므로 사용처가 많습니다.

구조는 간단합니다. 하나의 `EC2` 혹은 리눅스 서버에 엔진엑스 1대와 **스프링 부트 Jar를 2대** 사용하는 것입니다.

- 엔진엑스는 `80(http)`, `443(https)` 포트를 할당합니다.
- `스프링 부트1`은 `8081`포트로 실행합니다.
- `스프링 부트2`는 `8082`포트로 실행합니다.

**엔진엑스 무중단 배포 1**은 다음과 같은 구조가 됩니다.

![Chapter10_nginx_무중단배포_1](https://user-images.githubusercontent.com/68052095/101275875-79c9a600-37ec-11eb-98e8-8bf136781f9a.png)

운영 과정은 다음과 같습니다.

- ① 사용자는 서비스 주소로 접속합니다(`80` 혹은 `443` 포트).
- ② 엔진엑스는 사용자의 요청을 받아 현재 연결된 스프링 부트로 요청을 전달합니다.
    - 스프링 부트1 즉, 8081 포트로 요청을 전달한다고 가정합니다.
- ③ 스프링 부트2는 엔진엑스와 연결된 상태가 아니니 요청받지 못합니다.

1.1 버전으로 신규 배포가 필요하면, 엔진엑스와 연결되지 않은 스프링 부트2(8082 포트)로 배포합니다(아래 사진).

![Chapter10_nginx_무중단배포_2](https://user-images.githubusercontent.com/68052095/101275874-79310f80-37ec-11eb-85ce-4684565138c6.png)

- ① 배포하는 동안에도 서비스는 중단되지 않습니다.
    - 엔진엑스는 스프링 부트1을 바라보기 때문입니다.
- ② 배포가 끝나고 정상적으로 스프링 부트2가 구동 중인지 확인합니다.
- ③ 스프링 부트2가 정상 구동 중이면 `nginx reload` 명령어를 통해 `8081` 대신에 `8082`를 바라보도록 합니다.
- ④ `nginx reload`는 0.1초 이내에 완료됩니다.

이후 1.2 버전 배포가 필요하면 이번에는 스프링 부트1로 배포합니다(아래 사진).

![Chapter10_nginx_무중단배포_3](https://user-images.githubusercontent.com/68052095/101275873-79310f80-37ec-11eb-85e5-f727663f69ca.png)

- ① 현재는 엔진엑스와 연결된 것이 스프링 부트2입니다.
- ② 스프링 부트1의 배포가 끝났다면 엔진엑스가 스프링 부트1을 바라보도록 변경하고 `nginx reload`를 실행합니다.
- ③ 이후 요청부터는 엔진엑스가 스프링 부트 1로 요청을 전달합니다.

이렇게 구성하게 되면 전체 시스템 구조는 다음과 같습니다.

![Chapter10_nginx_무중단배포_전체_구조](https://user-images.githubusercontent.com/68052095/101275872-77ffe280-37ec-11eb-88eb-c1f6e69b8fa6.png)

기존 구조에서 `EC2` 내부의 구조만 변경된 것이니 크게 걱정하지 않아도 됩니다.

사진 출처 : [기억보단 기록을](https://jojoldu.tistory.com/267)

---

## 10.2 엔진엑스 설치와 스프링 부트 연동하기
가장 먼저 `EC2`에 엔진엑스를 설치하겠습니다.

#### 엔진엑스 설치
`EC2`에 접속해서 다음 명령어로 엔진엑스를 설치합니다.

>sudo yum install nginx

설치가 완료되었으면 다음 명령어로 엔진엑스를 실행합니다.

>sudo service nginx start

엔진엑스가 잘 실행되었다면 다음과 같은 메시지를 볼 수 있습니다.

>Starting nginx: [  OK  ]

>###### 학습중 발생 오류 추가
>![Chapter10_nginx_service_start_error](https://user-images.githubusercontent.com/68052095/101275172-e346b600-37e6-11eb-94d4-2274484363a6.PNG)
>명령어로 `sudo service nginx start` 대신 `sudo systemctl start nginx` 를 사용
>멈추고 싶다면 `sudo systemctl stop nginx` 명령어를 이용하면 된다.
>상태를 확인하고 싶다면 `sudo systemctl status nginx` 명령어를 이용한다.
>
>참고 링크 : [AWS EC2에 NGINX 설치 및 사용하기](https://msyu1207.tistory.com/entry/AWS-EC2%EC%97%90-NGINX-%EC%84%A4%EC%B9%98-%EB%B0%8F-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)

외부에서 잘 노출되는지 확인해 보겠습니다.

#### 보안 그룹 추가
먼저 엔진엑스의 포트번호를 보안 그룹에 추가하겠습니다. 엔진엑스의 포트번호는 기본적으로 `80`입니다. 해당 포트 번호가 보안 그룹에 없으니 `[EC2 -> 보안 그룹 -> EC2 보안 그룹 선택 -> 인바운드 편집]`으로 차례로 이동해서 변경합니다.

![80번 포트를 보안 그룹에 추가]

![Chapter10_nginx_inbound_add](https://user-images.githubusercontent.com/68052095/101275268-b0e98880-37e7-11eb-8060-22478c0c4da5.PNG)

#### 리다이렉션 주소 추가
`8080`이 아닌 `80`포트로 주소가 변경되니 구글과 네이버 로그인에도 변경된 주소를 등록해야만 합니다. 기존에 등록된 리디렉션 주소에서 `8080` 부분을 제거하여 추가 등록합니다. 앞서 진행된 `Chapter8`을 참고하여 구글과 네이버에 차례로 등록합니다.

![Chapter10_nginx_google_domain_add](https://user-images.githubusercontent.com/68052095/101275320-1ccbf100-37e8-11eb-904a-d92e28c42419.png)

![Chapter10_nginx_naver_domain_add](https://user-images.githubusercontent.com/68052095/101275369-67e60400-37e8-11eb-80b3-b45109d9b84c.png)

추가한 후에는 `EC2`의 도메인으로 접근하되, **8080 포트를 제거하고** 접근해 봅니다. 즉, 포트번호 없이 도메인만 입력해서 브라우저에서 접속합니다.

>`80번 포트는 기본적으로 도메인에서 포트번호가 제거된 상태입니다.`

그럼 다음과 같이 엔진엑스 웹페이지를 볼 수 있습니다.

![Chapter10_nginx_home](https://user-images.githubusercontent.com/68052095/101275799-da0c1800-37eb-11eb-97b5-dc7cd1db5c99.PNG)

이제 스프링 부트와 연동해 보겠습니다.

#### 엔진엑스와 스프링 부트 연동
엔진엑스가 현재 실행 중인 스프링 부트 프로젝트를 바라볼 수 있도록 프록시 설정을 하겠습니다. 엔진엑스 설정 파일을 열어봅니다.

>sudo vim /etc/nginx/nginx.conf

설정 내용 중 `server` 아래의 `location /` 부분을 찾아서 다음과 같이 추가합니다.

![Chapter10_nginx_location](https://user-images.githubusercontent.com/68052095/101276215-85b66780-37ee-11eb-820a-dc838291c482.png)

>proxy_pass http://localhost:8080; ①
>proxy_set_header X-Real-IP \$remote_addr;
>proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for; ②
>proxy_set_header Host $http_host;

##### -----코드설명-----
**①  proxy_pass**
- 엔진엑스로 요청이 오면 `http://localhost:8080`로 전달합니다.

**② proxy_set_header XXX**
- 실제 요청 데이터를 `header`의 각 항목에 할당합니다.
- 예) `proxy_set_header X-Real-IP $remote_addr` : `Request Header`의 `X-Real-IP`에 요청자의 `IP`를 저장합니다.

##### -----------------------

수정이 끝났으면 `:wq` 명령어로 저장하고 종료해서, 엔진엑스를 재시작 하겠습니다.

>sudo service nginx restart

>###### 학습중 발생 오류 추가
>
>위의 동작, 정지와 같은 오류로 인해 `sudo systemctl restart nginx` 사용하여 해결.


다시 브라우저로 접속해서 엔진엑스 시작 페이지가 보이면 화면을 새로고침합니다.

엔진엑스가 스프링 부트 프로젝트를 프록시하는 것이 확인됩니다(기본 페이지가 보입니다). 본격적으로 무중단 배포 작업을 진행해 보겠습니다.

---

## 10.3 무중단 배포 스크립트 만들기
무중단 배포 스크립트 작업 전에 API를 하나 추가하겠습니다. 이 API는 이후 배포 시에 `8081`을 쓸지, `8082`를 쓸지 판단하는 기준이 됩니다.

### profile API 추가
`ProfileController`를 만들어 다음과 같이 간단한 API 코드를 추가합니다.

```java
package com.banjjoknim.book.springboot.web;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ProfileController {
    private final Environment env;

    @GetMapping("/profile")
    public String profile() {
        List<String> profiles = Arrays.asList(env.getActiveProfiles()); // ①

        List<String> realProfiles = Arrays.asList("real", "real1", "real2");

        String defaultProfile = profiles.isEmpty() ? "default" : profiles.get(0);

        return profiles.stream()
                .filter(realProfiles::contains)
                .findAny()
                .orElse(defaultProfile);
    }
}
```

##### -----코드설명-----
**① env.getActiveProfiles()**
- 현재 실행 중인 `ActiveProfile`을 모두 가져옵니다.
- 즉, `real`, `real1`, `real2`는 모두 배포에 사용될 `profile`이라 이 중 하나라도 있으면 그 값을 반환하도록 합니다.
- 실제로 이번 무중단 배포에서는 `real1`과 `real2`만 사용되지만, `step2`를 다시 사용해볼 수도 있으니 `real`도 남겨둡니다.
##### -----------------------

이 코드가 잘 작동하는지 테스트 코드를 작성해 보겠습니다. 해당 컨트롤러는 특별히 **스프링 환경이 필요하지는 않습니다.** 그래서 `@SpringBootTest` 없이 테스트 코드를 작성합니다.

```java
package com.banjjoknim.book.springboot.web;

import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfileControllerUnitTest {

    @Test
    public void real_profile이_조회된다() {
        //given
        String expectedProfile = "real";
        MockEnvironment env = new MockEnvironment();
        env.addActiveProfile(expectedProfile);
        env.addActiveProfile("oauth");
        env.addActiveProfile("real-db");

        ProfileController controller = new ProfileController(env);

        //when
        String profile = controller.profile();

        //then
        assertThat(profile).isEqualTo(expectedProfile);
    }

    @Test
    public void real_profile이_없으면_첫_번째가_조회된다() {
        //given
        String expectedProfile = "oauth";
        MockEnvironment env = new MockEnvironment();
        env.addActiveProfile(expectedProfile);
        env.addActiveProfile("real-db");

        ProfileController controller = new ProfileController(env);

        //when
        String profile = controller.profile();

        //then
        assertThat(profile).isEqualTo(expectedProfile);
    }

    @Test
    public void active_profile이_없으면_default가_조회된다() {
        //given
        String expectedProfile = "default";
        MockEnvironment env = new MockEnvironment();

        ProfileController controller = new ProfileController(env);

        //when
        String profile = controller.profile();

        //then
        assertThat(profile).isEqualTo(expectedProfile);
    }

}
```
`ProfileController`나 `Environment` 모두 **자바 클래스(인터페이스)**이기 때문에 쉽게 테스트할 수 있습니다. `Environment`는 인터페이스라 가짜 구현체인 `MockEnvironment`(스프링에서 제공)를 사용해서 테스트하면 됩니다.

이렇게 해보면 **생성자 DI가 얼마나 유용한지** 알 수 있습니다. 만약 `Environment`를 `@Autowired`로 `DI` 받았다면 **이런 테스트 코드를 작성하지 못 했습니다.** 항상 스프링 테스트를 해야했을 것입니다. 앞의 테스트가 다 통과했다면 컨트롤러 로직에 대한 이슈는 없습니다.

그리고 이 `/profile`이 **인증 없이도 호출될 수 있게** `SecurityConfig` 클래스에 제외 코드를 추가합니다.

```java
.antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/profile").permitAll();
```
##### -----코드설명-----
**① permitAll 마지막에 "/profile"이 추가됩니다.**
##### ----------------------

그리고 `SecurityConfig` 설정이 잘 되었는지도 테스트 코드로 검증합니다. 이 검증은 스프링 시큐리티 설정을 불러와야 하니 `@SpringBootTest`를 사용하는 테스트 클래스(`ProfileControllerTest`)를 하나 더 추가합니다.

```java
package com.banjjoknim.book.springboot.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProfileControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void profile은_인증없이_호출된다() {
        String expected = "default";

        ResponseEntity<String> response = restTemplate.getForEntity("/profile", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }
}
```
여기까지 모든 테스트가 성공했다면 깃허브로 푸시하여 배포 합니다. 배포가 끝나면 브라우저에서 `/profile`로 접속해서 `profile`이 잘 나오는지 확인합니다.

여기까지 잘 되었으면 잘 구성된 것이니 다음으로 넘어갑니다.

### real1, real2 profile 생성
현재 `EC2` 환경에서 실행되는 `profile`은 `real`밖에 없습니다. 해당 `profile`은 **Travis CI 배포 자동화를 위한** `profile`이니 무중단 배포를 위한 `profile` 2개(`real1`, `real2`)를 `src/main/resources` 아래에 추가합니다.

```java
//application-real1.properties

server.port=8081
spring.profiles.include=oauth,real-db
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
spring.session.store-type=jdbc
```

```java
//application-real2.properties

server.port=8082
spring.profiles.include=oauth,real-db
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
spring.session.store-type=jdbc
```

2개의 `profile`은 `real profile`과 크게 다른 점은 없지만, 한 가지가 다릅니다.

`server.port`가 `8080`이 아닌 `8081/8082`로 되어 있습니다. 이 부분만 주의해서 생성하고 생성된 후에는 깃허브로 푸시하면서 마무리합니다.

### 엔진엑스 설정 수정
무중단 배포의 핵심은 **엔진엑스 설정**입니다. 배포 때마다 엔진엑스의 프록시 설정(스프링 부트로 요청을 흘려보내는)이 순식간에 교체됩니다. 여기서 프록시 설정이 교체될 수 있도록 설정을 추가하겠습니다.

엔진엑스 설정이 모여있는 `/etc/nginx/conf.d/ `에 `service-url.inc`라는 파일을 하나 생성합니다.

>sudo vim /etc/nginx/conf.d/service-url.inc

그리고 다음 코드를 입력합니다.

>set \$service_url http://127.0.0.1:8080;

저장하고 종료한 뒤(`:wq`) 해당 파일은 엔진엑스가 사용할 수 있게 설정합니다. 다음과 같이 `nginx.conf` 파일을 열겠습니다.

>sudo vim /etc/nginx/nginx.conf

`location / `부분을 찾아 다음과 같이 변경합니다.

>include /etc/nginx/conf.d/service-url.inc;
>
>location / {
>        proxy_pass \$service_url;
>}

저장하고 종료한 뒤(`:wq`) **재시작**합니다.

> sudo service nginx restart

다시 브라우저에서 정상적으로 호출되는지 확인합니다. 확인되었다면 엔진엑스 설정까지 잘 된 것입니다.

### 배포 스크립트들 작성
먼저 `step2`와 중복되지 않기 위해 `EC2`에 `step3` 디렉토리를 생성합니다.

>mkdir ~/app/step3 && mkdir ~/app/step3/zip

무중단 배포는 앞으로 `step3`를 사용하겠습니다. 그래서 `appspec.yml` 역시 `step3`로 배포되도록 수정합니다.

```sh
version: 0.0
os: linux
files:
  - source: /
    destination: /home/ec2-user/app/step3/zip/
    overwrite: yes
```
무중단 배포를 진행할 스크립트들은 총 5개입니다.

- `stop.sh` : 기존 엔진엑스에 연결되어 있진 않지만, 실행 중이던 스프링 부트 종료
- `start.sh` : 배포할 신규 버전 스프링 부트 프로젝트를 `stop.sh`로 종료한 `profile`로 실행
- `health.sh` : `start.sh`로 실행시킨 프로젝트가 정상적으로 실행됐는지 체크
- `switch.sh` : 엔진엑스가 바라보는 스프링 부트를 최신 버전으로 변경
- `profile.sh` : 앞선 4개 스크립트 파일에서 공용으로 사용할 `profile` 과 포트 체크 로직

`appspec.yml`에 앞선 스크립트를 사용하도록 설정합니다.

```
hooks:
  AfterInstall:
    - location: stop.sh # 엔진엑스와 연결되어 있지 않은 스프링 부트를 종료합니다.
      timeout: 60
      runas: ec2-user
  ApplicationStart:
    - location: start.sh # 엔진엑스와 연결되어 있지 않은 Port로 새 버전의 스프링 부트를 시작합니다.
      timeout: 60
      runas: ec2-user
  ValidateService:
    - location: health.sh # 새 스프링 부트가 정상적으로 실행됐는지 확인합니다.
      timeout: 60
      runas: ec2-user
```
`Jar` 파을이 복사된 이후부터 차례로 앞선 스크립트들이 실행된다고 보면 됩니다. 다음은 각 스크립트입니다. 이 스크립트들 역시 `scripts` 디렉토리에 추가합니다.

![Chapter10_scripts](https://user-images.githubusercontent.com/68052095/101280263-7db8f080-380b-11eb-9ac8-d0e81bb7397c.PNG)

#### `profile.sh`
```sh
#!/usr/bin/env bash

# 쉬고 있는 profile 찾기: real1이 사용 중이면 real2가 쉬고 있고, 반대면 real1이 쉬고 있음

function find_idle_profile() {
  RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/profile) ①

  if [ ${RESPONSE_CODE} -ge 400 ] # 400 보다 크면(즉, 40x/50x 에러 모두 포함)
  then
      CURRENT_PROFILE=real2
  else
      CURRENT_PROFILE=$(curl -s http://localhost/profile)
  fi

  if [ ${CURRENT_PROFILE} == real1 ]
  then
      IDLE_PROFILE=real2 ②
  else
      IDLE_PROFILE=real1
  fi

  echo "${IDLE_PROFILE}" ③
}

# 쉬고 있는 profile의 port 찾기

function find_idle_port() {
    IDLE_PROFILE=$(find_idle_profile)

    if [ ${IDLE_PROFILE} == real1 ]
    then
      echo "8081"
    else
      echo "8082"
    fi
}
```
##### -----코드설명-----
**① `$(curl -s -o /dev/null -w "%{http_code}" http://localhost/profile)`**
- 현재 엔진엑스가 바라보고 있는 스프링 부트가 정상적으로 수행 중인지 확인합니다.
- 응답값을 `HttpStatus`로 받습니다.
- 정상이면 `200`, 오류가 발생한다면 `400 ~ 503` 사이로 발생하니 `400` 이상은 모두 예외로 보고 `real2`를 **현재 profile로 사용**합니다.

**② `IDLE_PROFILE`**
- 엔진엑스와 연결되지 않은 `profile`입니다.
- 스프링 부트 프로젝트를 이 `profile`로 연결하기 위해 반환합니다.

**③ `echo "${IDLE_PROFILE}"`**
- `bash`라는 스크립트는 **값을 반환하는 기능이 없습니다.**
- 그래서 **제일 마지막 줄에 echo로 결과를 출력** 후, 클라이언트에서 그 값을 잡아서 (`$(find_idle_profile)`) 사용합니다.
- 중간에 `echo`를 사용해선 안 됩니다.
##### ----------------------

#### **`stop.sh`**
```sh
#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH) ①
source ${ABSDIR}/profile.sh ②

IDLE_PORT=$(find_idle_port)

echo "> $IDLE_PORT 에서 구동중인 애플리케이션 pid 확인"
IDLE_PID=$(lsof -ti tcp:${IDLE_PORT})

if [ -z ${IDLE_PID} ]
then
  echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $IDLE_PID"
  kill -15 ${IDLE_PID}
  sleep 5
fi
```
##### -----코드설명-----
**① `ABSDIR=$(dirname $ABSPATH)`**
- 현재 `stop.sh`가 속해 있는 경로를 찾습니다.
- 하단의 코드와 같이 `profile.sh`의 경로를 찾기 위해 사용됩니다.

**② `source ${ABSDIR}/profile.sh`**
- 자바로 보면 일종의 `import` 구문입니다.
- 해당 코드로 인해 `stop.sh`에서도 `profile.sh`의 여러 `function`을 사용할 수 있게 됩니다.
##### -----------------------

#### **`start.sh`**
```sh
#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

REPOSITORY=/home/ec2-user/app/step3
PROJECT_NAME=SpringBootWebService

echo "> Build 파일 복사"
echo "> cp $REPOSITORY/zip/*.jar $REPOSITORY/"

cp $REPOSITORY/zip/*.jar $REPOSITORY/

echo "> 새 애플리케이션 배포"
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

IDLE_PROFILE=$(find_idle_profile)

echo "> $JAR_NAME 를 profile=$IDLE_PROFILE 로 실행합니다."

nohup java -jar \
        -Dspring.config.location=classpath:/application.properties,classpath:/application-$IDLE_PROFILE.properties,/home/ec2-user/app/application-oauth.properties,/home/ec2-user/app/application-real-db.properties \
        -Dspring.profiles.active=$IDLE_PROFILE \
        $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &
```

##### -----코드설명-----
**① `기본적인 스크립트는 step2의 deploy.sh와 유사합니다`**
**② `다른 점이라면 IDLE_PROFILE을 통해 properties 파일을 가져오고(application-$IDLE_PROFILE.properties), active profile을 지정하는 것(-Dspring.profiles.active=$IDLE_PROFILE) 뿐입니다.`**
**③ `여기서도 IDLE_PROFILE을 사용하니 profile.sh을 가져와야 합니다.`**
##### ----------------------

#### **`health.sh`**
```sh
#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh
source ${ABSDIR}/switch.sh

IDLE_PORT=$(find_idle_port)

echo "> Health Check Start!"
echo "> IDLE_PORT: $IDLE_PORT"
echo "> curl -s http://localhost:$IDLE_PORT/profile "
sleep 10

for RETRY_COUNT in {1..10}
do
  RESPONSE=$(curl -s http://localhost:${IDLE_PORT}/profile)
  UP_COUNT=$(echo ${RESPONSE} | grep 'real' | wc -l)

  if [ ${UP_COUNT} -ge 1 ]
  then # UP_COUNT >= 1 ("real" 문자열이 있는지 검증)
      echo "> Health Check 성공"
      switch_proxy
      break
  else
      echo "> Health Check의 응답을 알 수 없거나 혹은 실행 상태가 아닙니다."
      echo "> Health Check: ${RESPONSE}"
  fi

  if [ ${RETRY_COUNT} -eq 10 ]
  then
    echo "> Health Check 실패. "
    echo "> 엔진엑스에 연결하지 않고 배포를 종료합니다."
    exit 1
  fi

  echo "> Health Check 연결 실패. 재시도...."
  sleep 10
done
```
##### -----코드설명-----
**① 엔진엑스와 연결되지 않은 포트로 스프링 부트가 잘 수행되었는지 체크합니다.**
**② 잘 떴는지 확인되어야 엔진엑스 프록시 설정을 변경(`switch_proxy`)합니다.**
**③ 엔진엑스 프록시 설정 변경은 `switch.sh`에서 수행합니다.**
##### ----------------------

#### `switch.sh`

```sh
#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

function switch_proxy() {
    IDLE_PORT=$(find_idle_port)

    echo "> 전환할 Port: $IDLE_PORT"
    echo "> Port 전환"
    echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" | sudo tee /etc/nginx/conf.d/service-url.inc
    
    echo "> 엔진엑스 Reload"
    sudo service nginx reload
}
```
##### -----코드설명-----
**① `echo "set \$service_url http://127.0.0.1:${IDLE_PORT};"`**
- 하나의 문장을 만들어 파이프라인(`|`)으로 넘겨주기 위해 `echo`를 사용합니다.
- 엔진엑스가 변경할 프록시 주소를 생성합니다.
- 쌍따옴표(`"`)를 사용해야 합니다.
- 사용하지 않으면 `$service_url`을 그대로 인식하지 못하고 변수를 찾게 됩니다.

**② `| sudo tee /etc/nginx/conf.d/service-url.inc`**
- 앞에서 넘겨준 문장을 `service-url.inc`에 덮어씌웁니다.

**③ `sudo service nginx reload`**
- 엔진엑스 설정을 다시 불러옵니다.
- **`restart`와는 다릅니다.**
- `restart`는 잠시 끊기는 현상이 있지만, `reload`는 끊김 없이 다시 불러옵니다.
- 다만, 중요한 설정들은 반영되지 않으므로 `restart`를 다시 사용해야 합니다.
- 여기선 **외부의 설정 파일**인 `service-url`을 다시 불러오는 거라 `reload`로 가능합니다.

스크립트까지 모두 완성했습니다. 그럼 실제로 무중단 배포를 진행해 보겠습니다.

---

## 10.4 무중단 배포 테스트
배포 테스트를 하기 전, 한 가지 추가 작업을 진행하도록 하겠습니다. 잦은 배포로 `Jar` 파일명이 겹칠 수 있습니다. 매번 버전을 올리는 것이 귀찮으므로 자동으로 버전값이 변경될 수 있도록 조치하겟습니다.

#### `build.gradle`
```java
version '1.0.1-SNAPSHOT-'+new Date().format("yyyyMMddHHmmss")
```
##### -----코드설명-----
**① `build.gradle`은 `Groovy` 기반의 빌드툴입니다.**
**② 당연히 `Groovy` 언어의 여러 문법을 사용할 수 있는데, 여기서는 `new Date()`로 빌드할 때마다 그 시간이 버전에 추가되도록 구성하였습니다.**
##### ----------------------

여기까지 구성한 뒤 최종 코드를 깃허브로 푸시합니다. 배포가 자동으로 진행되면 `CodeDeploy` 로그로 잘 진행되는지 확인해 봅니다.

>`tail -f /opt/codedeploy-agent/deployment-root/deployment-logs/codedeploy-agent-deployments.log`

그럼 다음과 같은 메시지가 차례로 출력됩니다.

![Chapter10_deployments_log](https://user-images.githubusercontent.com/68052095/101282400-53216480-3818-11eb-9d7b-c844c5f37f3f.PNG)

스프링 부트 로그도 보고 싶다면 다음 명령어로 확인할 수 있습니다.

>`vim ~/app/step3/nohup.out`

그럼 스프링 부트 실행 로그를 직접 볼 수 있습니다. 한 번 더 배포하면 그때는 `real2`로 배포됩니다. 이 과정에서 브라우저 새로고침을 해보면 전혀 중단 없는 것을 확인할 수 있습니다. 2번 배포를 진행한 뒤에 다음과 같이 자바 애플리케이션 실행 여부를 확인합니다.

>`ps -ef | grep java`

다음과 같이 2개의 애플리케이션(`real1`, `real2`)이 실행되고 있음을 알 수 있습니다.

![Chapter10_grep_java](https://user-images.githubusercontent.com/68052095/101282469-aa273980-3818-11eb-87e5-3fde7e548517.png)

이제 이 시스템은 마스터 브랜치에 푸시가 발생하면 자동으로 서버 배포가 진행되고, 서버 중단 역시 전혀 없는 시스템이 되었습니다.

---

#### 추가사항
실습 중간중간에 스크립트에 문제가 있는 것인지 새롭게 배포를 할 때마다 기존에 실행되어있던 프로젝트가 종료되지 않았고, 그로 인해 `java.net.BindException: Address already in use (Bind failed)` 에러가 발생하여 제대로 되지 않았다. 이 에러는 8080포트로 이미 실행되어 있는 `PID`를 찾아서 종료해준 뒤 배포하면 제대로 배포가 되는 것을 볼 수 있다.

참고 링크 
- [Address already in use 혹은 Bind failed 에러 해결하기](https://fishpoint.tistory.com/3746)
- [Address already in use (Bind failed) 에러 해결하기](https://philip1994.tistory.com/6)
- [netstat 명령어를 통한 네트워크 상태 확인 방법](http://blog.naver.com/PostView.nhn?blogId=ncloud24&logNo=221388026417&parentCategoryNo=&categoryNo=79&viewDate=&isShowPopularPosts=false&from=postView)