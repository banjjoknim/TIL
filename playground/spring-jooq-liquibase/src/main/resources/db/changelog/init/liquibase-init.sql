CREATE TABLE `LIQUIBASE_INIT`
(
    id          INT UNSIGNED AUTO_INCREMENT,
    author      varchar,
    description varchar,
    init_at     DATETIME DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO `LIQUIBASE_INIT` (author, description) VALUES ('banjjoknim', '리퀴베이스로 데이터베이스 초기화')
