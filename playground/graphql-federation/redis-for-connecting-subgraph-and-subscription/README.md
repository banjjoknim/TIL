# GraphQL Federation Subscription Redis

## 실행 방법

1. 이미지 빌드
```shell
docker build -t <your-image-tag> .
```

2. 빌드한 이미지 실행
```shell
docker run --name <your-container-name> -d -p 6379:6379 <your-image-tag> 
```

## 참고 자료

- [DockerHub/redis](https://hub.docker.com/_/redis)
