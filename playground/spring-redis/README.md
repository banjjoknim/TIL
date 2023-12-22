# Spring-redis

Spring + Redis 통합 환경 구성 예제

## Run Project

### Redis Run

문서 작성 당시 `redis latest version`은 `6.2.6` 임.

```shell
docker run --name spring-redis -p 6379:6379 redis
```

## Docs.

### Redis

- [CONFIG SET | Redis](https://redis.io/commands/config-set/)
- [Redis keyspace notifications](https://redis.io/docs/manual/keyspace-notifications/)

### Spring Data Redis

- [Spring Data Redis](https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/)
- [Spring Data Redis - 13.4. Keyspaces](https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#redis.repositories.keyspaces)

### Redisson

- [redisson/redisson GitHub](https://github.com/redisson/redisson/)
- [Redisson Wiki](https://github.com/redisson/redisson/wiki/Table-of-Content)
  - [2. Configuration](https://github.com/redisson/redisson/wiki/2.-Configuration)
  - [4. Data Serialization](https://github.com/redisson/redisson/wiki/4.-data-serialization)
  - [8. Distributed locks and synchronizers](https://github.com/redisson/redisson/wiki/8.-distributed-locks-and-synchronizers/#81-lock)
  - [11. Redis commands mapping](https://github.com/redisson/redisson/wiki/11.-Redis-commands-mapping)

## 참고 자료

- [레디스와 분산 락(1/2) - 레디스를 활용한 분산 락과 안전하고 빠른 락의 구현](https://hyperconnect.github.io/2019/11/15/redis-distributed-lock-1.html)
- [Redis로 분산 락을 구현해 동시성 이슈를 해결해보자!](https://hudi.blog/distributed-lock-with-redis/)
- [[SpringBoot] Redis Key Expired Event Notification](https://moonsiri.tistory.com/87)
- [Redis Keyspace Notifications for Expired Keys:](https://sauravomar01.medium.com/redis-keyspace-notifications-for-expired-keys-f38c18484a89)
- [Redis — Getting Notified When a Key is Expired or Changed](https://medium.com/nerd-for-tech/redis-getting-notified-when-a-key-is-expired-or-changed-ca3e1f1c7f0a)
- [Why is Lettuce the default Redis client used in Spring Session Redis?](https://github.com/spring-projects/spring-session/issues/789)
- [Jedis 보다 Lettuce 를 쓰자](https://jojoldu.tistory.com/418)
- [Lettuce Reference Guide](https://lettuce.io/core/release/reference/)
- [redisson-examples/objects-examples/src/main/java/org/redisson/example/objects/TopicExamples.java at master · redisson/redisson-examples](https://github.com/redisson/redisson-examples/blob/master/objects-examples/src/main/java/org/redisson/example/objects/TopicExamples.java)
- [Redisson 분산락을 이용한 동시성 제어](https://velog.io/@hgs-study/redisson-distributed-lock)

