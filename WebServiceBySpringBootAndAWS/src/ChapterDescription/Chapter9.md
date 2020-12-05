# Chapter9. 코드가 푸시되면 자동으로 배포해 보자 - Travis CI 배포 자동화
24시간 365일 운영되는 서비스에서 배포 환경 구축은 필수 과제 중 하나입니다. 여러 개발자의 코드가 **실시간으로** 병합되고, 테스트가 수행되는 환경, `master` 브랜치가 푸시되면 배포가 자동으로 이루어지는 환경을 구착하지 않으면 실수할 여지가 너무나 많습니다. 이번에는 이러한 배포 환경을 구성해 보겠습니다.

---

## 9.1 CI & CD 소개
앞에서 스크립트를 개발자가 직접 실행함으로써 발생하는 불편을 경험했습니다. 그래서 `CI`, `CD` 환경을 구축하여 이 과정을 개선하려고 합니다.

`CI`와 `CD`란 무엇일까? 코드 버전 관리를 하는 `VCS 시스템(Git, SVN 등)`에 `PUSH`가 되면 자동으로 테스트와 빌드가 수행되어 **안정적인 배포 파일을 만드는 과정**을 `CI(Continuous Integration - 지속적 통합)`라고 하며, 이 빌드 결과를 자동으로 운영 서버에 무중단 배포까지 진행되는 과정을 `CD(Continuous Deployment - 지속적인 배포)`라고 합니다.

여기서 주의할 점은 단순히 **CI 도구를 도입했다고 해서 CI를 하고 있는 것은 아닙니다.** [마틴 파울러의 블로그](http://bit.ly/2Yv0vFp)를 참고해 보면 `CI`에 대해 다음과 같은 4가지 규칙을 이야기합니다.

- 모든 소스 코드가 살아 있고(현재 실행되고) 누구든 현재의 소스에 접근할 수 있는 단일 지점을 유지할 것
- 빌드 프로세스를 자동화해서 누구든 소스로부터 시스템을 빌드하는 단일 명령어를 사용할 수 있게 할 것
- 테스팅을 자동화해서 단일 명령어로 언제든지 시스템에 대한 건전한 테스트 수트를 실핼할 수 있게 할 것
- 누구나 현재 실행 파일을 얻으면 지금까지 가장 완전한 실행 파일을 얻었다는 확신을 하게 할 것

여기서 특히나 중요한 것은 **테스팅 자동화**입니다. 지속적으로 통합하기 위해서는 무엇보다 이 프로젝트가 **완전한 상태임을 보장**하기 위해 테스트 코드가 구현되어 있어야만 합니다.

- 추천 강의 : [백명석님의 클린코더스 - TDD편](http://bit.ly/2xtKinX)

---

## 9.2 Travis CI 연동하기
`Travis CI`는 깃허브에서 제공하는 무료 `CI` 서비스입니다. 젠킨스와 같은 `CI` 도구도 있지만, 젠킨스는 설치형이기 때문에 이를 위한 `EC2` 인스턴스가 하나 더 필요합니다. 이제 시작하는 서비스에서 배포를 위한 `EC2` 인스턴스는 부담스럽기 때문에 오픈소스 웹 서비스인 `Travis CI`를 사용하겠습니다.

>`AWS`에서 `Travis CI`와 같은 `CI` 도구로 `CodeBuild`를 제공합니다. 하지만 빌드 시간만큼 요금이 부과되는 구조라 초기에 사용하기는 부담스럽습니다. 실제 서비스되는 `EC2`, `RDS`, `S3` 외에는 비용 부분을 최소화하는 것이 좋습니다.

### Travis CI 웹 서비스 설정
`https://travis-ci.org/`에서 깃허브 계정으로 로그인을 한 뒤, 오른쪽 위에 `[계정명 -> Settings]`를 클릭합니다.

설정 페이지 아래쪽을 보면 깃허브 저장소 검색창이 있습니다. 여기서 저장소 이름을 입력해서 찾은 다음, 오른쪽의 상태바를 활성화시킵니다(`Legacy Services integration 메뉴`).

활성화한 저장소를 클릭하면 다음과 같이 저장소 빌드 히스토리 페이지로 이동합니다.

![Chapter9_Travis_CI1](https://user-images.githubusercontent.com/68052095/100978996-89e04c00-3586-11eb-811a-1bd8f61d38d8.PNG)

`Travis CI` 웹 사이트에서 설정은 이것이 끝입니다. 상세한 설정은 **프로젝트의 yml 파일로** 진행해야 합니다.

### 프로젝트 설정
`Travis CI`의 상세한 설정은 프로젝트에 존재하는 `.travis.yml` 파일로 할 수 있습니다. `yml` 파일 확장자를 `YAML(야믈)`이라고 합니다. `YAML`은 쉽게 말해서 **JSON에서 괄호를 제거한** 것입니다.

프로젝트의 `build.gradle`과 같은 위치에서 `.travis.yml`을 생성한 후 다음의 코드를 추가합니다.

**.travis.yml**
```java
language: java
jdk:
  - openjdk8

branches: // 1.
  only:
    - master

# Travis CI 서버의 Home
cache: // 2.
  directories:
    - '$HOME/.m2/repository'
    - '$HOME/.gradle'

script: "./gradlew clean build" // 3.

# 실행 완료 시 메일로 알람
notifications: // 4.
  email:
    - recipients: 본인 메일 주소
```
##### 코드설명
**1. branches**
- `Travis CI`를 어느 브랜치가 푸시될 때 수행할지 지정합니다.
- 현재 옵션은 오직 **master 브랜치에 push될 때만** 수행합니다.

**2. cache**
- 그레이들을 통해 의존성을 받게 되면 이를 해당 디렉토리에 캐시하여, **같은 의존성은 다음 배포 때부터 다시 받지 않도록** 설정합니다.

**3. script**
- `master` 브랜치에 푸시되었을 때 수행하는 명령어입니다.
- 여기서는 프로젝트 내부에 둔 `gradlew`을 통해 `clean & build`를 수행합니다.

**4. notifications**
- `Travis CI` 실행 완료 시 자동으로 알람이 가도록 설정합니다.

자 그럼 여기까지 마친 뒤, `master` 브랜치에 커밋과 푸시를 하고, 좀 전의 `Travis CI` 저장소 페이지를 확인합니다.

>###### 학습중 발생 오류 추가
>계속해서 아무 변화가 없길래 여기저기 다 찾아봤지만 해결책을 찾을 수 없었다. 
>그러다가 [Travis](https://travis-ci.org/getting_started)의  2번 항목에서 해결책을 찾을 수 있었다.
>
>**Add a .travis.yml file to your repository**
>
>In order for Travis CI to build your project, you'll need to add a .travis.yml
> configuration file to the root directory of your repository.
>If a .travis.yml is not in your repository, or is not valid YAML, Travis CI will ignore it.
>Here you can find some of our basic language examples.
>
>그러니까 쉽게 말하면, `repository`의 `root` 디렉토리에 `.travis.yml` 파일을 만들어야만 한다는 것이다. 나의 경우에는 프로젝트의 상위에 `TIL`이라는 `root` 디렉토리가 있었기 때문에 계속해서 안된 것이었다...

>###### 학습중 발생 오류 추가
>![Chapter9_Travis_CI_queued](https://user-images.githubusercontent.com/68052095/100988702-57891b80-3593-11eb-919b-3264e5f39f5e.PNG)
>빌드가 되는줄 알았더니... Queued에서 멈춰버렸다.
>[Jobs stuck on "Queued"](https://travis-ci.community/t/jobs-stuck-on-queued/5768)를 참고해보니, 존재하지 않는 환경을 요청하는 경우 발생한다고 한다...
>따라서 환경에 대한 설정을 입력해주어야 한다.

>###### 학습중 발생 오류 추가
>![Chapter9_Travis_CI_Permission_Denied](https://user-images.githubusercontent.com/68052095/101017595-a2b02800-35ad-11eb-8183-5789e5206f41.PNG)
>`gradlew`는 실행 파일이다. 그리고 리눅스 환경에서 실행 파일은 **실행 권한이 있어야만 실행 가능**한데, 실행권한이 없기 때문에 발생한 에러라고 한다(접근권한이 아니라).
>일반적으로 `gradlew` 파일에는 실행권한이 프로젝트 생성시점에 부여되며, **그 파일이 깃허브에 올라가기 때문에** 별도로 `x권한(실행)`을 주지 않아도 된다.
>
>결론적으로, `gradlew`에 실행권한이 자동으로 부여되지 않았기 때문에 발생한 오류인 것 같다. 따라서 해결책으로 `.travis.yml`에 
>`before_install: `
>`- chmod +x gradlew`
>를 추가해서 해결했다.
>[참고 링크](https://github.com/jojoldu/freelec-springboot2-webservice/issues/75)

빌드가 성공한 것이 확인되면 `.travis.yml`에 등록한 이메일을 확인합니다.

![Chapter9_Travis_CI_Success](https://user-images.githubusercontent.com/68052095/101044598-dcd7f480-35c2-11eb-89dd-c2f946e45831.PNG)

#### 성공!!

---

## 9.3 Travis CI와 AWS S3 연동하기
`S3`란 `AWS`에서 제공하는 **일종의 파일 서버**입니다. 이미지 파일을 비롯한 정적 파일들을 관리하거나 지금 진행하는 것처럼 배포 파일들을 관리하는 등의 기능을 지원합니다. 보통 이미지 업로드를 구현한다면 이 `S3`를 이용하여 구현하는 경우가 많습니다. `S3`를 비롯한 `AWS` 서비스와 `Travis CI`를 연동하게 되면 전체 구조는 다음과 같습니다.

![Chapter9_Travis_CI_연동시_구조](https://user-images.githubusercontent.com/68052095/101052929-8a023b00-35ca-11eb-8dac-fa79d321910e.png)

첫 번째 단계로 `Travis CI`와 `S3`를 연동하겠습니다. 실제 배포는 `AWS CodeDeploy`라는 서비스를 이용합니다. 하지만, `S3` 연동이 먼저 필요한 이유는 **jar 파일을 전달하기 위해서**입니다.

`CodeDeploy`는 저장 기능이 없습니다. 그래서 `Travis CI`가 빌드한 결과물을 받아서 `CodeDeploy`가 가져갈 수 있도록 보관할 수 있는 공간이 필요합니다. 보통은 이럴 때 `AWS S3`를 이용합니다.

>`CodeDeploy`가 빌드도 하고 배포도 할 수 있습니다. `CodeDeploy`에서는 깃허브 코드를 가져오는 기능을 지원하기 때문입니다. 하지만 이렇게 할 때 빌드 없이 배포만 필요할 때 대응하기 어렵습니다.
>
>빌드와 배포가 분리되어 있으면 예전에 빌드되어 만들어진 `Jar`를 재사용하면 되지만, `CodeDeploy`가 모든 것을 하게 될 땐 항상 빌드를 하게 되니 확장성이 많이 떨어집니다. 그래서 웬만하면 빌드와 배포는 분리하는 것을 추천합니다.

`Travis CI`와 `AWS S3` 연동을 진행해 보겠습니다.

### AWS Key 발급
일반적으로 `AWS` 서비스에 **외부 서비스가 접근할 수 없습니다.** 그러므로 **접근 가능한 권한을 가진 Key**를 생성해서 사용해야 합니다. `AWS`에서는 이러한 인증과 관련된 기능을 제공하는 서비스로 `IAM(Identity and Access Management)`이 있습니다.

`IAM`은 `AWS`에서 제공하는 서비스의 접근 방식과 권한을 관리합니다. 이 `IAM`을 통해 `Travis CI`가 `AWS`의 `S3`와 `CodeDeploy`에 접근할 수 있도록 해보겠습니다. `AWS` 웹 콘솔에서 `IAM`을 검색하여 이동합니다. `IAM` 페이지 왼쪽 사이드바에서 `[사용자 -> 사용자 추가]` 버튼을 차례로 클릭합니다.

생성할 사용자의 이름과 액세스 유형을 선택합니다. 액세스 유형은 **프로그래밍 방식 엑세스**입니다.

권한 설정 방식은 3개중 `[기존 정책 직접 연결]`을 선택합니다.

화면 아래 정책 검색 화면에서 `s3full`로 검색하여 체크하고 다음 권한으로 `CodeDeployFull`을 검색하여 체크합니다.

실제 서비스 회사에서는 권한도 **S3와 CodeDeploy를 분리해서 관리**하기도 합니다만, 여기서는 간단하게 둘을 합쳐서 관리하겠습니다. 2개의 권한이 설정되었으면 다음으로 넘어갑니다.

태그는 `Name` 값을 지정하는데, 본인이 인지 가능한 정도의 이름으로 만듭니다.

마지막으로 본인이 생성한 권한 설정 항목을 확인한 뒤, 최종 생성 완료되면 다음과 같이 액세스 키와 비밀 액세스 키가 생성됩니다. 이 두 값이 **Travis CI에서 사용될 키**입니다.

이제 이 키를 `Travis CI`에 등록하겠습니다.

### Travis CI에 키 등록
먼저 `Travis CI`의 설정 화면으로 이동합니다(오른쪽의 `More options`의 `Settings` 버튼으로 진입합니다).

![Chapter9_Travis_CI_Settings](https://user-images.githubusercontent.com/68052095/101055258-19a8e900-35cd-11eb-93ec-9a9ff7db786c.PNG)

설정 화면을 아래로 조금 내려보면 `Environment Variables` 항목이 있습니다.

여기에 `AWS_ACCESS_KEY`, `AWS_SECRET_KEY`를 변수로 해서 `IAM` 사용자에서 발급받은 키 값들을 등록합니다.

|`NAME`에 입력 | `VALUE`에 입력|
|--|--|
|AWS_ACCESS_KEY | 액세스 키 ID |
|AWS_SECRET_KEY | 비밀 액세스 키|

각각 입력한 후 `Add` 버튼을 통해 추가하면 됩니다.

여기에 등록된 값들은 이제 `.travis.yml` 에서 `$AWS_ACCESS_KEY`, `$AWS_SECRET_KEY`란 이름으로 사용할 수 있습니다.

그럼 이제 이 키를 사용해서 `Jar`를 관리할 `S3` 버킷을 생성하겠습니다.

### S3 버킷 생성
다음으로 `S3(Simple Storage Service)`에 관해 설정을 진행하겠습니다. `AWS`의 `S3` 서비스는 일종의 **파일 서버**입니다. 순수하게 파일들을 저장하고 접근 권한을 관리, 검색 등을 지원하는 파일 서버의 역할을 합니다.

`S3`는 보통 게시글을 쓸 때 나오는 첨부파일 등록을 구현할 때 많이 이용합니다. 파일 서버의 역할을 하기 때문인데, `Travis CI`에서 생성된 **Build 파일을 저장**하도록 구성하겠습니다. `S3`에 저장된 `Build` 파일은 이후 `AWS`의 `CodeDeploy`에서 배포할 파일로 가져가도록 구성할 예정입니다. `AWS` 서비스에서 `S3`를 검색하여 이동하고 버킷을 생성합니다(버킷 만들기 버튼 이용).

원하는 버킷명을 작성합니다. 이 버킷에 **배포할 Zip 파일이 모여있는 장소**임을 의미하도록 짓는 것을 추천합니다.

그리고 버전관리를 설정합니다. 별다른 설정을 할 것이 없으니 바로 넘어갑니다.

다음으로는 버킷의 보안과 권한 설정 부분입니다. 퍼블릭 액세스를 열어두지 말고 **모든 차단**을 해야 합니다. 현재 프로젝트는 깃허브에 오픈소스로 풀려있으니 문제없지만, 실제 서비스에서 할 때는 `Jar` 파일이 퍼블릭일 경우 누구나 내려받을 수 있어 코드나 설정값, 주요 키값들이 다 탈취될 수 있습니다.

퍼블릭이 아니더라도 우리는 `IAM` 사용자로 발급받은 키를 사용하니 접근 가능합니다. 그러므로 모든 액세스를 차단하는 설정에 체크합니다.

그리고 버킷을 생성하면 버킷 목록에서 확인할 수 있습니다.

`S3`가 생성되었으니 이제 이 `S3`로 배포 파일을 전달해 보겠습니다.

### .travis.yml 추가
`Travis CI`에서 빌드하여 만든 `Jar` 파일을 `S3`에 올릴 수 있도록 `.travis.yml`에 다음 코드를 추가합니다.

```java
...
before_deploy:
  - zip -r springboot2-webservice *
  - mkdir -p deploy
  - mv springboot2-webservice.zip deploy/springboot2-webservice.zip

deploy:
  provider: s3
  access_key_id: $AWS_ACCESS_KEY # Travis repo settings에 설정된 값
  secret_access_key: $AWS_SECRET_KEY # Travis repo settings에 설정된 값

  bucket: banjjoknim-springboot2-webservice-build # S3 버킷
  region: ap-northeast-2
  skip_cleanup: true
  acl: private # zip 파일 접근을 private로
  local_dir: deploy # before_deploy에서 생성한 디렉토리
  wait-until-deployed: true
...
```
전체 코드는 다음과 같습니다. `Travis CI Settings` 항목에서 등록한 `$AWS_ACCESS_KEY`와 `$AWS_SECRET_KEY`가 변수로 사용됩니다.

```java
language: java

jdk:
  - openjdk11 # 저는 프로젝트 자바 버전이 11입니다.

branches:
  only:
    - master

# Travis CI 서버의 Home
cache:
  directories:
    - '$HOME/.m2/repository'
    - '$HOME/.gradle'

# Permission Denied 오류 해결을 위해 추가
before_install:
  - chmod +x gradlew

script: "./gradlew clean build"

before_deploy: // 1.
  - zip -r springboot2-webservice * // 2.
  - mkdir -p deploy // 3.
  - mv springboot2-webservice.zip deploy/springboot2-webservice.zip // 4.

deploy: // 5.
  provider: s3 
  access_key_id: $AWS_ACCESS_KEY # Travis repo settings에 설정된 값
  secret_access_key: $AWS_SECRET_KEY # Travis repo settings에 설정된 값

  bucket: banjjoknim-springboot2-webservice-build # S3 버킷
  region: ap-northeast-2
  skip_cleanup: true
  acl: private # zip 파일 접근을 private로
  local_dir: deploy # before_deploy에서 생성한 디렉토리 // 6.
  wait-until-deployed: true

# 실행 완료 시 메일로 알람
notifications:
  email:
    - recipients: 본인 메일 주소
```

##### -----코드설명-----
**1. before_deploy**
- `deploy` 명령어가 실행되기 전에 수행됩니다.
- `CodeDeploy`는 **Jar 파일은 인식하지 못하므로** `Jar+기타 설정 파일들`을 모아 압축(`zip`) 합니다.

**2. zip -r springboot2-webservice**
- 현재 위치의 모든 파일을 `springboot2-webservice` 이름으로 압축(`zip`) 합니다.
- 명령어의 마지막 위치는 본인의 프로젝트 이름이어야 합니다.

**3. mkdir -p deploy**
- `deploy`라는 디렉토리를 `Travis CI`가 실행 중인 위치에서 생성합니다.

**4. mv springboot2-webservice.zip deploy/springboot2-webservice.zip**
- `springboot2-webservice.zip` 파일을 `deploy/springboot2-webservice.zip`으로 이동시킵니다.

**5. deploy**
- `S3`로 파일 업로드 혹은 `CodeDeploy`로 배포 등 **외부 서비스와 연동될 행위들을 선언**합니다.

**6. local_dir: deploy**
- 앞에서 생성한 `deploy` 디렉토리를 지정합니다.
- **해당 위치의 파일들만** `S3`로 전송합니다.

##### ----------------------

설정이 다 되었으면 **깃허브로 푸시**합니다. `Travis CI`에서 자동으로 빌드가 진행되는 것을 확인하고, 모든 빌드가 성공하는지 확인합니다. 다음 로그가 나온다면 `Travis CI`의 빌드가 성공한 것입니다.

![Chapter9_Travis_CI_Success2](https://user-images.githubusercontent.com/68052095/101118381-48a27780-362c-11eb-8058-6778ffb8d28a.PNG)

그리고 `S3` 버킷을 가보면 업로드가 성공한 것을 확인할 수 있습니다.

`Travis CI`를 통해 자동으로 파일이 올려진 것을 확인할 수 있습니다.
`Travis CI`와 `S3` 연동이 완료되었습니다. 이제 `CodeDeploy`로 배포까지 완료해 보겠습니다.

---

## 9.4 Travis CI와 AWS S3, CodeDeploy 연동하기
`AWS`의 배포 시스템인 `CodeDeploy`를 이용하기 전에 배포 대상인 **EC2가 CodeDeploy를 연동 받을 수 있게** `IAM` 역할을 하나 생성하겠습니다.

### EC2에 IAM 역할 추가하기
`S3`와 마찬가지로 `IAM`을 검색하고, 이번에는 `[역할]` 탭을 클릭해서 이동합니다. `[역할 -> 역할 만들기]` 버튼을 차례로 클릭합니다.

##### -----`IAM`의 사용자와 역할의 차이점-------
- 역할
    - `AWS` 서비스에만 할당할 수 있는 권한
    - `EC2`, `CodeDeploy`, `SQS` 등
- 사용자
    - **AWS 서비스 외**에 사용할 수 있는 권한
    - `로컬 PC`, `IDC 서버` 등
##### ------------------------------------------------------

지금 만들 권한은 **EC2에서 사용할 것**이기 때문에 사용자가 아닌 역할로 처리합니다. 서비스 선택에서는 `[AWS 서비스 -> EC2]`를 차례로 선택합니다.

정책에선 `EC2RoleForA`를 검색하여 `AmazonEC2RoleforAWSCodeDeploy`를 선택한 뒤, 태그는 본인이 원하는 이름으로 짓습니다.

마지막으로 역할의 이름을 등록하고 나머지 등록 정보를 최종적으로 확인한 뒤 역할을 생성합니다.

이렇게 만든 역할을 `EC2` 서비스에 등록하겠습니다. `EC2` 인스턴스 목록으로 이동한 뒤, 본인의 인스턴스를 마우스 오른쪽 버튼으로 눌러 `[인스턴스 설정 -> IAM 역할 연결/바꾸기]`를 차례로 선택합니다(현재는 `[인스턴스 선택후 -> 작업 -> 보안 -> IAM 역할 수정]`을 이용하면 되는듯하다).

방금 생성한 역할을 선택한 뒤, 해당 `EC2` 인스턴스를 재부팅 합니다. 재부팅을 해야만 역할이 정상적으로 적용되니 꼭 한 번은 재부팅해 주세요.

재부팅이 완료되었으면 `CodeDeploy`의 요청을 받을 수 있게 에이전트를 하나 설치하겠습니다.

### CodeDeploy 에이전트 설치
`EC2`에 접속해서 다음 명령어를 입력합니다.

>aws s3 cp s3://aws-codedeploy-ap-northeast-2/latest/install . --region ap-northeast-2

내려받기가 성공했다면 다음과 같은 메시지가 콘솔에 출력됩니다.

>download: s3://aws-codedeploy-ap-northeast-2/latest/install to ./install

`install` 파일에 실행 권한이 없으니 실행 권한을 추가합니다.

>chmod +x ./install

`install` 파일로 설치를 진행합니다(아래 명령어 입력).

>sudo ./install auto

>###### 학습중 오류 발생 추가
>![Chapter9_CodeDeploy_install_error](https://user-images.githubusercontent.com/68052095/101120672-97064500-3631-11eb-8cdd-7ac30c2e854b.PNG)
>**루비라는 언어가 설치되지 않은 상태**여서 발생하는 에러이다.
>`Linux AMI`에서는 `sudo yum install ruby` 명령어를 실행해서 루비를 설치하면 해결된다.
>
>아래 사진은 `Ubuntu` 기준으로 루비를 설치하는 방법이다.
>![Chapter9_CodeDeploy_install_error_solution](https://user-images.githubusercontent.com/68052095/101120759-c9b03d80-3631-11eb-99a3-381bd4f9a9cb.PNG)

설치가 끝났으면 `Agent`가 정상적으로 실행되고 있는지 상태 검사를 합니다.

>sudo service codedeploy-agent status

다음과 같이 `running` 메시지가 출력되면 정상입니다.

>The AWS CodeDeploy agent is running as PID xxx

### CodeDeploy를 위한 권한 생성
`CodeDeploy`에서 `EC2`에 접근하려면 마찬가지로 권한이 필요합니다. `AWS`의 서비스이니 `IAM` 역할을 생성합니다. 서비스는 `[AWS 서비스 -> CodeDeploy]`를 차례로 선택합니다.

`CodeDeploy`는 권한이 하나뿐이라서 선택 없이 바로 다음으로 넘어갑니다.

태그 역시 본인이 원하는 이름으로 짓습니다.

`CodeDeploy`를 위한 역할 이름과 선택 항목들을 확인한 뒤 생성 완료를 합니다.

이제 `CodeDeploy`를 생성해 보겠습니다.

### CodeDeploy 생성
`CodeDeploy`는 `AWS`의 배포 삼형제 중 하나입니다. 배포 삼형제에 대해 간단히 소개하자면 다음과 같습니다.

- `Code Commit`
  - 깃허브와 같은 코드 저장소의 역할을 합니다.
  - 프라이빗 기능을 지원한다는 강점이 있지만, 현재 **깃허브에서 무료로 프라이빗 지원**을 하고 있어서 거의 사용되지 않습니다.

- `Code Bulid`
  - `Travis CI`와 마찬가지로 **빌드용 서비스**입니다.
  - 멀티 모듈을 배포해야 하는 경우 사용해 볼만하지만, 규모가 있는 서비스에서는 대부분 **젠킨스/팀시티 등을 이용**하니 이것 역시 사용할 일이 없습니다.

- `CodeDeploy`
  - `AWS`의 배포 서비스입니다.
  - 앞에서 언급한 다른 서비스들은 대체재가 있고, 딱히 대체재보다 나은 점이 없지만, `CodeDeploy`는 대체재가 없습니다.
  - 오토 스케일링 그룹 배포, 블루 그린 배포, `EC2` 단독 배포 등 많은 기능을 지원합니다.

이 중에서 현재 진행 중인 프로젝트에서는 `Code Commit`의 역할은 깃허브가, `Code Build`의 역할은 `Travis CI`가 하고 있습니다. 그래서 우리가 추가로 사용할 서비스는 `CodeDeploy`입니다.

`CodeDeploy` 서비스로 이동해서 화면 중앙에 있는 `[애플리케이션 생성]` 버튼을 클릭한 뒤, 생성할 `CodeDeploy`의 이름과 컴퓨팅 플랫폼을 선택합니다. 컴퓨팅 플랫폼은 `[EC2/온프레미스]`를 선택하면 됩니다.

생성이 완료되면 배포 그룹을 생성합니다. 화면 중앙의 `[배포 그룹 생성]` 버튼을 클릭합니다.

배포 그룹과 서비스 역할을 등록합니다. 서비스 역할은 좀 전에 생성한 `CodeDeploy`용 `IAM` 역할을 선택하면 됩니다.

배포 유형에서는 **현재 위치**를 선택합니다. 만약 본인이 배포할 서비스가 2대 이상이라면 `블루/그린`을 선택하면 됩니다. 여기선 1대의 `EC2`에만 배포하므로 선택하지 않습니다.

환경 구성에서는 `[Amazon EC2 인스턴스]`에 체크한 뒤, 해당하는 인스턴스의 키와 값을 선택해줍니다.

마지막으로 배포 구성 중에서 `CodeDeployDefault.AllAtOnce`를 선택하고 `로드밸런싱 활성화` 항목은 체크 해제합니다.

배포 구성이란 한번 배포할 때 몇 대의 서버에 배포할지를 결정합니다. 2대 이상이라면 1대씩 배포할지, 30% 혹은 50%로 나눠서 배포할지 등등 여러 옵션을 선택하겠지만, 1대 서버다 보니 전체 배포하는 옵션으로 선택하면 됩니다.

배포 그룹까지 생성되었다면 `CodeDeploy` 설정은 끝입니다. 이제 `Travis CI`와 `CodeDeploy`를 연동해 보겠습니다.

### Travis CI, S3, CodeDeploy 연동
먼저 `S3`에서 넘겨줄 `zip` 파일을 저장할 디렉토리를 하나 생성하겠습니다. `EC2` 서버에 접속해서 다음과 같이 디렉토리를 생성합니다.

>mkdir ~/app/step2 && mkdir ~/app/step2/zip

`Travis CI`의 `Build`가 끝나면 `S3`에 `zip` 파일이 전송되고, 이 `zip` 파일은 `/home/ec2-user/app/step2/zip`로 복사되어 압축을 풀 예정입니다.

`Travis CI`의 설정은 **.travis.yml로 진행**했습니다.

`AWS CodeDeploy`의 설정은 **appspec.yml로 진행**합니다(`.travis.yml`과 동일 위치에 생성).

코드는 다음과 같습니다.

```java
version: 0.0 // 1.
os: linux
files:
  - source: / // 2.
    destination: /home/ec2-user/app/step2/zip/ // 3.
    overwrite: yes // 4.
```
##### -----코드설명-----
**1. version: 0.0**
- `CodeDeploy 버전`을 이야기합니다.
- 프로젝트 버전이 아니므로 `0.0` 외에 다른 버전을 사용하면 오류가 발생합니다.

**2. source**
- `CodeDeploy`에서 전달해 준 파일 중 `destination`으로 이동시킬 대상을 지정합니다.
- 루트 경로(`/`)를 지정하면 전체 파일을 이야기합니다.

**3. destination**
- `source`에서 지정된 파일을 받을 위치입니다.
- 이후 `Jar`를 실행하는 등의 작업은 `destination`에서 옮긴 파일들로 진행됩니다.

**4. overwirte**
- 기존에 파일들이 있으면 덮어쓸지를 결정합니다.
- 현재 `yes`라고 했으니 파일들을 덮어쓰게 됩니다.

##### ------------------------

`.travis.yml`에도 `CodeDeploy` 내용을 추가합니다. `deploy` 항목에 다음 코드를 추가합니다.

```java
...
- provider: codedeploy
    access_key_id: $AWS_ACCESS_KEY
    secret_access_key: $AWS_SECRET_KEY
    bucket: banjjoknim-springboot2-webservice-build
    key: springboot2-webservice.zip # 빌드 파일을 압축해서 전달
    bundle_type: zip # 압축 확장자
    application: springboot2-webservice # 웹 콘솔에서 등록한 CodeDeploy 애플리케이션
    deployment_group: springboot2-webservice-group # 웹 콘솔에서 등록한 CodeDeploy 배포 그룹
    region: ap-northeast-2
    wait-until-deployed: true
```
`S3` 옵션과 유사합니다. 다른 부분은 `CodeDeploy`의 애플리케이션 이름과 배포 그룹명을 지정하는 것입니다.

전체 코드는 다음과 같습니다.

```java
language: java

jdk:
  - openjdk11

branches:
  only:
    - master

# Travis CI 서버의 Home
cache:
  directories:
    - '$HOME/.m2/repository'
    - '$HOME/.gradle'

# Permission Denied 오류 해결을 위해 추가
before_install:
  - chmod +x gradlew

script: "./gradlew clean build"

before_deploy:
  - zip -r springboot2-webservice *
  - mkdir -p deploy
  - mv springboot2-webservice.zip deploy/springboot2-webservice.zip

deploy:
  - provider: s3
    access_key_id: $AWS_ACCESS_KEY # Travis repo settings에 설정된 값
    secret_access_key: $AWS_SECRET_KEY # Travis repo settings에 설정된 값

    bucket: banjjoknim-springboot2-webservice-build # S3 버킷
    region: ap-northeast-2
    skip_cleanup: true
    acl: private # zip 파일 접근을 private로
    local_dir: deploy # before_deploy에서 생성한 디렉토리
    wait-until-deployed: true

  - provider: codedeploy
    access_key_id: $AWS_ACCESS_KEY # Travis repo settings에 설정된 값
    secret_access_key: $AWS_SECRET_KEY # Travis repo settings에 설정된 값
    bucket: banjjoknim-springboot2-webservice-build # S3 버킷
    key: springboot2-webservice.zip # 빌드 파일을 압축해서 전달
    bundle_type: zip # 압축 확장자
    application: springboot2-webservice # 웹 콘솔에서 등록한 CodeDeploy 애플리케이션
    deployment_group: springboot2-webservice-group # 웹 콘솔에서 등록한 CodeDeploy 배포 그룹
    region: ap-northeast-2
    wait-until-deployed: true

# 실행 완료 시 메일로 알람
notifications:
  email:
    - recipients: 본인 메일 주소
```
모든 내용을 작성했다면 프로젝트를 커밋하고 푸시합니다. 깃허브로 푸시가 되면 `Travis CI`가 자동으로 시작됩니다.
`Travis CI`가 끝나면 `CodeDeploy` 화면 아래에서 배포가 수행되는 것을 확인할 수 있습니다(그룹 배포 내역).

>###### 학습중 발생 오류 추가
>![Chapter9_Travis_deploy_fali_by_ruby_version](https://user-images.githubusercontent.com/68052095/101242974-577a4e80-3740-11eb-8c2e-425da8609207.PNG)
>CodeDeploy 환경구성에 Amazon EC2 인스턴스 설정이 잘못되어서 생긴 문제라고 한다
(태그가 잘못되어서 생긴 문제).
>또는 `travis.yml`에 오타가 있어서 트레비스 트리거가 작동하지 않아 발생한 오류라고 한다.
>나의 경우에는 아래 사진에 표시된 부분이 달라서 트리거가 작동하지 않은 것 같다.
>![Chapter9_Travis_deploy_fali_by_ruby_version2](https://user-images.githubusercontent.com/68052095/101245049-8ea32c80-374d-11eb-8b1b-ad2ffccf6455.png)
>참고 링크 : [travis ci , s3, codeDeploy 연동 실패](https://github.com/jojoldu/freelec-springboot2-webservice/issues/474), [Travis CI, AWS S3, AWS CodeDeploy 배포 오류](https://jhhj424.tistory.com/16)

![Chapter9_CodeDeploy_success!](https://user-images.githubusercontent.com/68052095/101129462-ef930d80-3644-11eb-9fa0-d8b425cd574c.PNG)

배포가 끝났다면 다음 명령어로 파일들이 잘 도착했는지 확인해 봅니다.

>cd /home/ec2-user/app/step2/zip

파일 목록을 확인해 봅니다(`ls -al`)

프로젝트 파일들이 잘 도착한게 확인된다면 `Travis CI`와 `S3`, `CodeDeploy`의 연동이 완료된 것입니다.

---

## 9.5 배포 자동화 구성
`Travis CI`, `S3`, `CodeDeploy` 연동까지 구현되었습니다. 이제 이것을 기반으로 실제로 **Jar를 배포하여 실행까지 해보겠습니다.**

### `deploy.sh` 파일 추가
먼저 `step2` 환경에서 실행될 `deploy.sh`를 생성하겠습니다. `scripts` 디렉토리를 생성해서 여기에 스크립트를 생성합니다(`scripts`의 위치는 `build.gradle`과 동일한 위치).

![Chapter9_scripts_directory_create](https://user-images.githubusercontent.com/68052095/101125214-097c2280-363c-11eb-839c-5628c8735983.PNG)

>#!/bin/bash
>
>REPOSITORY=/home/ec2-user/app/step2
>PROJECT_NAME=springboot2-webservice
>
>echo "> Build 파일 복사"
>
>cp \$REPOSITORY/zip/*.jar $REPOSITORY/
>
>echo "> 현재 구동 중인 애플리케이션 pid 확인"
>
>CURRENT_PID=\$(pgrep -fl springboot2-webservice | grep jar | awk '{print $1}') // ①
>
>echo "현재 구동 중인 애플리케이션 pid : $CURRENT_PID"
>
>if [ -z "\$CURRENT_PID"]; then
>  echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
>else
>  echo "> kill -15 $CURRENT_PID"
>  kill -15 \$CURRENT_PID
>  sleep 5
>fi
>
>echo "> 새 애플리케이션 배포"
>
>JAR_NAME=$(ls -tr \$REPOSITORY/*.jar | tail -n 1)
>
>echo "> JAR_NAME 에 실행권한 추가"
>
>chmod +x $JAR_NAME // ②
>
>echo "> $JAR_NAME 실행"
>
>nohup java -jar \\
>-Dspring.config.location=classpath:/application.properties,classpath:/application-real.properties,/home/ec2-user/app/application-oauth.properties,/home/ec2-user/app/application-real-db.properties \\
>-Dspring.profiles.active=real \\
>\$JAR_NAME > $REPOSITORY/nohup.out 2>&1 & // ③

##### -----코드설명-----
**① CURRENT_PID**
- 현재 수행 중인 스프링 부트 애플리케이션의 프로세스 ID를 찾습니다.
- 실쟁 중이면 종료하기 위해서입니다.
- 스프링 부트 애플리케이션 이름(`springboot2-webservice`)으로 된 다른 프로그램들이 있을 수 있어 `springboot2-webservice`로 된 `jar(pgrep -fl springboot2-webservice | grep jar)` 프로세스를 찾은 뒤 ID를 찾습니다(`| awk '{print $1}`)

**② chmod +x $JAR_NAME**
- `Jar` 파일은 실행 권한이 없는 상태입니다.
- `nohup`으로 실행할 수 있게 실행 권한을 부여합니다.

**③ \$JAR_NAME > $REPOSITORY/nohup.out 2>&1 &**
- `nohup` 실행 시 `CodeDeploy`는 **무한 대기**합니다.
- 이 이슈를 해결하기 위해 `nohup.out` 파일을 표준 입출력용으로 별도로 사용합니다.
- 이렇게 하지 않으면 `nohup.out` 파일이 생기지 않고, **CodeDeploy 로그에 표준 입출력이 출력됩니다.**
- `nohup`이 끝나기 전까지 `CodeDeploy`도 끝나지 않으니 꼭 저렇게 해야합니다.

##### ------------------------

`step1`에서 작성된 `deploy.sh`와 크게 다르지 않습니다. 우선 `git pull`을 통해 **직접 빌드했던 부분을 제거**했습니다. 그리고 `Jar`를 실행하는 단계에서 몇 가지 코드가 추가되었습니다.

>플러그인 중 `BashSupport`를 설치하면 `.sh` 파일 편집 시 도움을 받을 수 있습니다.

`deploy.sh` 파일은 여기에서 끝입니다.
다음으로 `.travis.yml` 파일을 수정하겠습니다.

### .travis.yml 파일 수정
현재는 프로젝트의 모든 파일을 `zip` 파일로 만드는데, 실제로 필요한 파일들은 **Jar, appspec.yml, 배포를 위한 스크립트**들입니다. 이 외 나머지는 배포에 필요하지 않으니 포함하지 않겠습니다. 그래서 `.travis.yml` 파일의 `before_deploy`를 수정합니다.

>`.travis.yml` 파일은 `Travis CI`에서만 필요하지 `CodeDeploy`에서는 필요하지 않습니다.

>before_deploy:
>  - mkdir -p before-deploy # zip에 포함시킬 파일들을 담을 디렉토리 설정 // ①
>  - cp scripts/*.sh before-deploy/ // ②
>  - cp appspec.yml before-deploy/
>  - cp build/libs/*.jar before-deploy/
>  - cd before-deploy && zip -r before-deploy * # before-deploy로 이동 후 전체 압축 // ③
>  - cd ../ && mkdir -p deploy # 상위 디렉토리로 이동 후 deploy 디렉토리 생성
>  - mv before-deploy/before-deploy.zip deploy/sprinbboot2-webservice.zip # deploy로 zip파일 이동

##### -----코드설명-----
**① Travis CI는 S3로 특정 파일만 업로드가 안됩니다.**
- 디렉토리 단위로만 업로드할 수 있기 때문에 `deploy` 디렉토리는 항상 생성합니다.

**② before-deploy에는 zip 파일에 포함시킬 파일들을 저장합니다.**

**③ zip -r 명령어를 통해 before-deploy 디렉토리 전체 파일을 압축합니다.**

##### ----------------------

이 외 나머지 코드는 수정할 것이 없습니다.

마지막으로 `CodeDeploy`의 명령을 담당할 `appspec.yml` 파일을 수정합니다.

### appspec.yml 파일 수정
`appspec.yml` 파일에 다음 코드를 추가합니다. `location`, `timeout`, `runas`의 들여쓰기를 주의해야 합니다. 들여쓰기가 잘못될 경우 배포가 실패합니다.

```java
permissions:
  - object: /
    pattern: "**"
    owner: ec2-user
    group: ec2-user
    
hooks:
  ApplicationStart:
    - location: deploy.sh
      timeout: 60
      runas: ec2-user
```

##### -----코드설명-----
**① permissions**
- `CodeDeploy`에서 `EC2` 서버로 넘겨준 파일들을 모두 `ec2-user` 권한을 갖도록 합니다.

**② hooks**
- `CodeDeploy` 배포 단계에서 실행할 명령어를 지정합니다.
- `ApplicationStart`라는 단계에서 `deploy.sh`를 `ec2-user` 권한으로 실행하게 합니다.
- `timeout: 60`으로 스크립트 실행 60초 이상 수행되면 실패가 됩니다(무한정 기다릴 수 없으니 시간 제한을 둬야만 합니다).

##### -----------------------

그래서 전체 코드는 다음과 같습니다.
```java
version: 0.0
os: linux
files:
  - source: /
    destination: /home/ec2-user/app/step2/zip/
    overwrite: yes

permissions:
  - object: /
    pattern: "**"
    owner: ec2-user
    group: ec2-user

hooks:
  ApplicationStart:
    - location: deploy.sh
      timeout: 60
      runas: ec2-user
```
모든 설정이 완료되었으니 깃허브로 커밋과 푸시를 합니다. `Travis CI`에서 다음과 같이 성공 메시지를 확인하고 `CodeDeploy`에서도 배포가 성공한 것을 확인합니다.

![Chapter9_final_deploy_success](https://user-images.githubusercontent.com/68052095/101137164-bb721980-3651-11eb-8b09-769732b2e225.PNG)

![Chapter9_final_deploy_success2](https://user-images.githubusercontent.com/68052095/101137299-f70ce380-3651-11eb-8c69-3a67f409ded2.png)

웹 브라우저에서 `EC2` 도메인을 입력해서 확인해 봅니다.

마지막으로 실제 배포하듯이 진행해 보겠습니다.

### 실제 배포 과정 체험
`build.gradle`에서 프로젝트 버전을 변경합니다.

>version '1.0.1-SNAPSHOT'

간단하게나마 변경된 내용을 알 수 있게 `src/main/resources/templates/index.mustache` 내용에 다음과 같이 `Ver.2` 텍스트를 추가합니다.

>...
>`<h1>스프링 부트로 시작하는 웹 서비스 Ver.2</h1>`
>...

그리고 깃허브로 커밋과 푸시를 합니다. 그럼 **변경된 코드가 배포**된 것을 확인할 수 있습니다.

---

## 9.6 CodeDeploy 로그 확인
`CodeDeploy`와 같이 `AWS`가 지원하는 서비스에서는 오류가 발생했을 때 로그 찾는 방법을 모르면 오류를 해결하기가 어렵습니다. 그래서 배포가 실패하면 어느 로그를 봐야 할지 간단하게 소개하려고 합니다.

`CodeDeploy`에 관한 대부분 내용은 `/opt/codedeploy-agent/deployment-root`에 있습니다. 해당 디렉토리로 이동(`cd /opot/codedeploy-agent/deployment-root`)한 뒤 목록을 확인해보면 다음과 같은 내용을 확인할 수 있습니다.

![Chapter9_deployment_logs](https://user-images.githubusercontent.com/68052095/101147661-05fa9280-3660-11eb-8e6f-830eea9baf7f.PNG)

##### 코드설명
**① 최상단의 영문과 대시(-)가 있는 디렉토리명은 CodeDeploy ID입니다.**
- 사용자마다 고유한 ID가 생성되어 각자 다른 ID가 발급되니 본인의 서버에는 다른 코드로 되어있습니다.
- 해당 디렉토리로 들어가 보면 **배포한 단위별로 배포 파일들이** 있습니다.
- 본인의 배포 파일이 정상적으로 왔는지 확인해 볼 수 있습니다.

**② /opt/codedeploy-agent/deployment-root/deployment-logs/codedeploy-agent-deployments.log**
- `CodeDeploy` 로그 파일입니다.
- `CodeDeploy`로 이루어지는 배포 내용 중 표준 입/출력 내용은 모두 여기에 담겨 있습니다.
- 작성한 `echo` 내용도 모두 표기됩니다.

테스트, 빌드, 배포까지 전부 자동화되었습니다. 이제는 작업이 끝난 내용을 **Master 브랜치에 푸시만 하면 자동으로 EC2에 배포**가 됩니다.

하지만 문제가 한 가지 남았습니다. **배포하는 동안** 스프링 부트 프로젝트는 종료 상태가 되어 **서비스를 이용할 수 없다**는 것입니다. 

다음으로는 **서비스 중단 없는 배포** 방법을 소개하려고 합니다. 흔히 말하는 무중단 배포라고 생각하면 됩니다.

---