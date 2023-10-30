# GraphQL Federation Subscription React Client Sample

## 실행 방법

### `터미널`을 이용해 실행할 경우

- 아래의 명령어를 순서대로 실행한다.

```shell
npm install --no-optional && npm cache clean --force
npm run start
```

### `Docker`를 이용해 실행할 경우

1. 아래 명령어를 통해 `Docker Image`를 빌드한다.

```shell
docker build -t <your-image-tag-name> .
```

2. 빌드된 `Docker Image`를 이용하여 `Docker Container`를 실행한다.

```shell
docker run -d -it --name <your-app-container-name> -p 3000:3000 <your-image-tag-name>
```

*주의* : 빌드된 `Docker Image`를 `docker run` 명령어로 실행시 `-it` 플래그를 입력하지 않으면 제대로 실행되지 않는다...

- [How to use this image](https://github.com/nodejs/docker-node#how-to-use-this-image)

## 참고 자료

- [docker_hub/node](https://hub.docker.com/_/node/)
- [nodejs/docker-node](https://github.com/nodejs/docker-node)

## 그 외

### Node.js Version Issue

- `Node.js` 이미지 버전으로 `node:20-alpine` 사용시 아래와 같은 에러 발생.

```text
Error: error:0308010C:digital envelope routines::unsupported

    at new Hash (node:internal/crypto/hash:68:19)

    at Object.createHash (node:crypto:138:10)

    at module.exports (/usr/src/app/node_modules/webpack/lib/util/createHash.js:135:53)

    at NormalModule._initBuildHash (/usr/src/app/node_modules/webpack/lib/NormalModule.js:417:16)

    at handleParseError (/usr/src/app/node_modules/webpack/lib/NormalModule.js:471:10)

    at /usr/src/app/node_modules/webpack/lib/NormalModule.js:503:5

    at /usr/src/app/node_modules/webpack/lib/NormalModule.js:358:12

    at /usr/src/app/node_modules/loader-runner/lib/LoaderRunner.js:373:3

    at iterateNormalLoaders (/usr/src/app/node_modules/loader-runner/lib/LoaderRunner.js:214:10)

    at iterateNormalLoaders (/usr/src/app/node_modules/loader-runner/lib/LoaderRunner.js:221:10)

/usr/src/app/node_modules/react-scripts/scripts/start.js:19

  throw err;

  ^


Error: error:0308010C:digital envelope routines::unsupported

    at new Hash (node:internal/crypto/hash:68:19)

    at Object.createHash (node:crypto:138:10)

    at module.exports (/usr/src/app/node_modules/webpack/lib/util/createHash.js:135:53)

    at NormalModule._initBuildHash (/usr/src/app/node_modules/webpack/lib/NormalModule.js:417:16)

    at /usr/src/app/node_modules/webpack/lib/NormalModule.js:452:10

    at /usr/src/app/node_modules/webpack/lib/NormalModule.js:323:13

    at /usr/src/app/node_modules/loader-runner/lib/LoaderRunner.js:367:11

    at /usr/src/app/node_modules/loader-runner/lib/LoaderRunner.js:233:18

    at context.callback (/usr/src/app/node_modules/loader-runner/lib/LoaderRunner.js:111:13)

    at /usr/src/app/node_modules/babel-loader/lib/index.js:59:103 {

  opensslErrorStack: [ 'error:03000086:digital envelope routines::initialization error' ],

  library: 'digital envelope routines',

  reason: 'unsupported',

  code: 'ERR_OSSL_EVP_UNSUPPORTED'

}
```

### ENV File

브라우저(크롬 등)는 서버처럼 `Docker Network 내부`가 아닌 `로컬 환경(Docker Network 내부가 아닌 Host 환경)`에서 열리는 것이기 때문에 아래와 같이 환경변수를 설정한다.

- `.env`

```text
REACT_APP_GATEWAY_API_URL=http://localhost:4000/graphq
REACT_APP_SUBSCRIPTIONS_API_URL=ws://localhost:8081/subscriptions
```
